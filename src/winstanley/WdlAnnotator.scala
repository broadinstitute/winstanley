package winstanley

import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import winstanley.psi._
import winstanley.structure.WdlImplicits._

import collection.JavaConverters._

class WdlAnnotator extends Annotator {

  /**
    * Makes sure that the passed PSI element is one of the cases, then determines if it's been declared somewhere
    */
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = psiElement match {
    case dep: LeafPsiElement if dep.getElementType == WdlTypes.DEPRECATED_COMMAND_VAR_OPENER =>
      val commandVar = dep.getParent.getParent
      annotationHolder.createWeakWarningAnnotation(commandVar.getTextRange, "Deprecated placeholder style: Use ~{ ... } from WDL draft 3 onwards to match 'command <<<' section placeholders")

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
        val valueNames = value.findReferencesInScope.flatMap(_.declaredValueName)

        if (!valueNames.contains(identifierText)) {
          annotationHolder.createErrorAnnotation(identifier.getTextRange, s"No declaration found for '${identifier.getText}'")
        }
      }
    case value: WdlCallableLookup =>
      // TODO (Issue 63): Work out a better way to verify that imports exist and (if local?) that an appropriate task/workflow inside them exist.
      // For now to avoid error highlights, I'll just suppress errors for things that look like FQNs
      if (value.getFullyQualifiedName.getNode.getChildren(null).length == 1) {
        value.getFullyQualifiedName.getIdentifierNode foreach { identifier =>
          val identifierText = identifier.getText
          val taskNames = value.findTasksInScope.flatMap(_.declaredValueName)

          if (!taskNames.contains(identifierText)) {
            annotationHolder.createErrorAnnotation(identifier.getTextRange, s"No task declaration found for '${identifier.getText}'")
          }
        }
      } else
        ()
    case declaration: WdlDeclaration =>
      if (psiElement.getWdlFileElement.isInstanceOf[WdlDraft3File])
        if (!declaration.getParent.isInstanceOf[WdlInputBlock] && declaration.getSetter == null)
          annotationHolder.createErrorAnnotation(psiElement, "Immediate assignment required for non-input declaration [draft-3]")
    case _ => ()
  }

  private def kvName(kv: WdlKv): Option[String] = Option(kv.getNode.findChildByType(WdlTypes.IDENTIFIER)).map(_.getText)
  private def runtimeKeyword(r: WdlRuntimeBlock): Option[ASTNode] = Option(r.getNode.findChildByType(WdlTypes.RUNTIME))
  private def outputKeyword(r: WdlTaskOutputs): Option[ASTNode] = Option(r.getNode.findChildByType(WdlTypes.OUTPUT))
}
