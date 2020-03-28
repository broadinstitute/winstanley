package winstanley.formatting

import com.intellij.formatting.{Indent, _}
import com.intellij.psi._
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.annotations._
import winstanley.WdlLanguage

class WdlFormattingModelBuilder extends FormattingModelBuilder {

  @NotNull
  def createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel =
    FormattingModelProvider.createFormattingModelForPsiFile(
      element.getContainingFile,
      new WdlBlock(
        element.getNode,
        Wrap.createWrap(WrapType.NONE, false),
        Alignment.createAlignment,
        new WdlSpacingBuilder(settings, WdlLanguage.INSTANCE),
        Indent.getNoneIndent
      ),
      settings
    )
}
