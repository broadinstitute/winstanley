package winstanley

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import winstanley.psi._
import winstanley.structure.WdlImplicits._

class WdlAnnotator extends Annotator {

  /**
    * Makes sure that the passed PSI element is one of the cases, then determines if it's been declared somewhere
    */
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = psiElement match {
    case dep: LeafPsiElement if dep.getElementType == WdlTypes.DEPRECATED_COMMAND_VAR_OPENER =>
      val commandVar = dep.getParent.getParent
      annotationHolder.createWarningAnnotation(commandVar.getTextRange, "Deprecated placeholder style: Use ~{ ... } from WDL draft 3 onwards to match 'command <<<' section placeholders")

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
}
