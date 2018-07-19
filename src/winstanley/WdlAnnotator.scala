package winstanley

import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.TokenSet
import winstanley.psi._
import winstanley.psi.impl.WdlTaskBlockImpl
import winstanley.structure.WdlImplicits._

import collection.JavaConverters._

class WdlAnnotator extends Annotator {

  /**
    * Makes sure that the passed PSI element is one of the cases, then determines if it's been declared somewhere
    */
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = psiElement match {
    case dep: LeafPsiElement if dep.getElementType == WdlTypes.DEPRECATED_COMMAND_VAR_OPENER =>
      val commandVar = dep.getParent.getParent
      annotationHolder.createWeakWarningAnnotation(commandVar.getTextRange, "Deprecated placeholder style: Use ~{ ... } for WDL 1.0 (draft-3) onwards to match 'command <<<' section placeholders")

    case task: WdlTaskBlock =>
      if (!task.getTaskSectionList.asScala.exists(section => Option(section.getCommandBlock).isDefined)) {
        annotationHolder.createErrorAnnotation(task.getTaskDeclaration.getNameIdentifier.getTextRange, "No command specified for task")
      }
      if (!task.getTaskSectionList.asScala.exists(section => Option(section.getRuntimeBlock).isDefined)) {
        annotationHolder.createWeakWarningAnnotation(task.getTaskDeclaration.getNameIdentifier.getTextRange, "Non-portable task section: add a runtime section specifying a docker image")
      }
      if (!task.getTaskSectionList.asScala.exists(section => Option(section.getTaskOutputs).isDefined)) {
        annotationHolder.createWeakWarningAnnotation(task.getTaskDeclaration.getNameIdentifier.getTextRange, "Suspicious lack of task outputs (is this task really idempotent and portable?)")
      }
    case runtime: WdlRuntimeBlock if !runtime.getMap.getKvList.asScala.flatMap(kvName).contains("docker") =>
      runtimeKeyword(runtime) foreach { r => annotationHolder.createWeakWarningAnnotation(r.getTextRange, "Non-portable runtime section: specify a docker image") }
    case taskOutputSection: WdlTaskOutputs if taskOutputSection.getOutputKvList.asScala.isEmpty =>
      outputKeyword(taskOutputSection) foreach { r => annotationHolder.createWeakWarningAnnotation(r.getTextRange, "Suspicious lack of task outputs (is this task really idempotent and portable?)") }

    case value: WdlValueLookup =>
      value.getIdentifierNode foreach { identifier =>
        val identifierText = identifier.getText
        val valueNames = value.findReferencesInScope.map(_.getNameIdentifier.getText)

        if (!valueNames.contains(identifierText)) {
          annotationHolder.createErrorAnnotation(identifier.getTextRange, s"No declaration found for '${identifier.getText}'")
        }
      }
    case value: WdlCallableLookup =>
      // TODO (Issue 63): Work out a better way to verify that imports exist and (if local?) that an appropriate task/workflow inside them exist.
      // For now to avoid error highlights, I'll just suppress errors for things that look like FQNs
      if (value.getFullyQualifiedName.getNode.getChildren(TokenSet.ANY).length == 1) {
        value.getFullyQualifiedName.getIdentifierNode foreach { identifier =>
          val identifierText = identifier.getText
          val taskNames = value.findTaskDeclarationsInScope.flatMap(_.declaredValueName)

          if (!taskNames.contains(identifierText)) {
            annotationHolder.createErrorAnnotation(identifier.getTextRange, s"No task declaration found for '${identifier.getText}'")
          }
        }
      } else
        ()
    case declaration: WdlDeclaration =>
      if (psiElement.getWdlFileElement.isInstanceOf[WdlVersion10File])
        if (!declaration.getParent.isInstanceOf[WdlInputBlock] && declaration.getSetter == null)
          annotationHolder.createErrorAnnotation(psiElement, "Immediate assignment required for non-input declaration: do you need to add an 'input { }' section? [WDL 1.0]")

    case wildcardOutput: WdlWfOutputWildcardStatement =>
      if (psiElement.getWdlFileElement.isInstanceOf[WdlDraft2File]) {
        annotationHolder.createWeakWarningAnnotation(wildcardOutput, "Declaration style outputs will be required in a later version of WDL")
      } else {
        annotationHolder.createErrorAnnotation(wildcardOutput, "Declaration style outputs are required in WDL 1.0 (draft 3) and later")
      }

    case callBlock: WdlCallBlock =>
      def taskForCall(callBlock: WdlCallBlock): Option[WdlTaskBlockImpl] = {
        val taskName = callBlock.getCallableLookup.getName
        val tasksInScope: Set[WdlTaskBlockImpl] = callBlock.findTasksInScope

        tasksInScope.find(_.getTaskDeclaration.getName == taskName)
      }

      if (psiElement.getWdlFileElement.isInstanceOf[WdlVersion10File]) {
        taskForCall(callBlock) match {
          case Some(task) =>
            // The "type" of a WdlTaskSection is determined by which one of its getXXX() methods returns not-null. Checkmate, Scala nerds.
            val maybeTaskInputSection: Option[WdlTaskSection] = task.getTaskSectionList.asScala.find(_.getInputBlock != null)

            val maybeCallInput: Option[WdlCallInput] = Option(callBlock.getCallInput)

            (maybeCallInput, maybeTaskInputSection) match {
              case (Some(callInput), Some(taskInputSection)) =>
                compareInputListsForCall(psiElement, annotationHolder, callInput, taskInputSection)
              case (Some(_), None) =>
                annotationHolder.createErrorAnnotation(psiElement, "Task does not take inputs.")
              case (None, Some(taskInputSection)) =>
                // Missing args = all args
                val missingArgs = taskInputSection.getInputBlock.getDeclarationList.asScala.map(_.getName)
                annotationHolder.createWeakWarningAnnotation(
                  psiElement,
                  "Unsupplied input(s) " + missingArgs.map("\'" + _ + "\'").mkString(", ") + " must be assigned here or, if this is the root workflow, provided in the inputs JSON."
                )
              case (None, None) => ()
            }

          case None => ()
        }
      }
    case _ => ()
  }

  private def kvName(kv: WdlKv): Option[String] = Option(kv.getNode.findChildByType(WdlTypes.IDENTIFIER)).map(_.getText)
  private def runtimeKeyword(r: WdlRuntimeBlock): Option[ASTNode] = Option(r.getNode.findChildByType(WdlTypes.RUNTIME))
  private def outputKeyword(r: WdlTaskOutputs): Option[ASTNode] = Option(r.getNode.findChildByType(WdlTypes.OUTPUT))

  private def compareInputListsForCall(psiElement: PsiElement, annotationHolder: AnnotationHolder, callInput: WdlCallInput, taskInputSection: WdlTaskSection) = {
    // In the interest of unambiguous naming, this method (ab)uses the formal definitions of parameter (declared on method) and argument (passed to method)
    val callArgumentMappings: List[WdlMapping] = callInput.getMappingList.asScala.toList
    val callArguments = callArgumentMappings.map(_.getNode.getText.split(Array(' ', '=')).head)

    // Check duplicate arguments
    val distinctArguments = callArguments.distinct
    val duplicateArgumentInstances = callArguments diff distinctArguments // For args ['a', 'b', 'c', 'c', 'c'] this is ['c', 'c']
    val duplicateArguments = duplicateArgumentInstances.distinct          // ['c', 'c'] -> ['c']

    duplicateArguments map { argument =>
      annotationHolder.createErrorAnnotation(
        psiElement,
        "Repeated input \'" + argument + "\'"
      )
    }

    val parameters: Seq[WdlDeclaration] = taskInputSection.getInputBlock.getDeclarationList.asScala

    val missingArgs: Seq[String] = parameters flatMap { parameter =>
      if (parameter.getTypeE.getText.endsWith("?") || parameter.getSetter != null) {
        None
      } else {
        if (!callArguments.contains(parameter.getName))
          Some(parameter.getName)
        else
          None
      }
    }

    // Multiple annotations on the same element are supported and work well visually
    if (missingArgs.nonEmpty)
      annotationHolder.createWeakWarningAnnotation(
        psiElement,
        "Unsupplied input(s) " + missingArgs.map("\'" + _ + "\'").mkString(", ") + " must be assigned here or, if this is the root workflow, provided in the inputs JSON."
      )

    val extraArgs: Seq[String] = callArguments flatMap { argument =>
      if (!parameters.map(_.getName).contains(argument))
        Some(argument)
      else
        None
    }

    if (extraArgs.nonEmpty)
      annotationHolder.createErrorAnnotation(
        psiElement,
        "Unexpected inputs(s) for task: " + extraArgs.mkString(", ")
      )
  }

}
