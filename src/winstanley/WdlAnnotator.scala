package winstanley

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import winstanley.psi.{WdlCallableLookup, WdlValueLookup}
import winstanley.structure.WdlImplicits._


class WdlAnnotator extends Annotator {

  /**
    * Makes sure that the passed PSI element is one of the cases, then determines if it's been declared somewhere
    */
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = psiElement match {
    case value: WdlValueLookup =>

      value.getIdentifierNode foreach { identifier =>
        val identifierText = identifier.getText
        val valueNames = value.findReferencesInScope.flatMap(_.declaredValueName)

        if (!valueNames.contains(identifierText)) {
          annotationHolder.createErrorAnnotation(identifier.getTextRange, s"No declaration found for '${identifier.getText}'")
        }
      }
    case value: WdlCallableLookup =>

      value.getFullyQualifiedName.getIdentifierNode foreach { identifier =>
        val identifierText = identifier.getText
        val taskNames = value.findTasksInScope.flatMap(_.declaredValueName)

        if (!taskNames.contains(identifierText)) {
          annotationHolder.createErrorAnnotation(identifier.getTextRange, s"No task declaration found for '${identifier.getText}'")
        }
      }
    case _ => ()
  }
}
