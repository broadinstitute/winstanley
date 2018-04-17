package winstanley

import com.intellij.lang.{BracePair, PairedBraceMatcher}
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import winstanley.psi.WdlTypes

/**
  * Provides brace-matching support in WDL by enumerating the types of braces that can be logically
  * considered as opening and closing each other.
  *
  * How to implement this is not especially well documented but this is based on this question:
  * https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000055450-How-to-make-my-plugin-to-highlight-a-matching-parenthesis-in-the-editor-of-a-language
  *
  * And this one:
  * https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000393430-Auto-closing-braces-in-a-custom-language-BraceMatcher-not-working
  *
  * And on the PairedBraceMatcher javadoc comments:
  * https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/lang/PairedBraceMatcher.java
  *
  * And on the BracePair javadoc comments:
  * https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/lang/BracePair.java
  *
  * It's pretty logical: we list the pairs of tokens that can open and close each other, and intellij will auto-insert the
  * closer when we type the opener, and highlight them together (having worked out any nesting too).
  */
class WdlBraceMatcher extends PairedBraceMatcher {
  /**
    * Below text copied from PairedBraceMatcher. We choose the "just return true" route...
    *
    * Returns true if paired rbrace should be inserted after lbrace of given type when lbrace is encountered before contextType token.
    * It is safe to always return true, then paired brace will be inserted anyway.
    * @param lbraceType lbrace for which information is queried
    * @param contextType token type that follows lbrace
    * @return true / false as described
    */
  override def isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType): Boolean = true

  /**
    * Below text copied from PairedBraceMatcher...
    *
    * Returns the array of definitions for brace pairs that need to be matched when
    * editing code in the language.
    *
    * @return the array of brace pair definitions.
    */
  override def getPairs: Array[BracePair] = Array(
    new BracePair(WdlTypes.DEPRECATED_COMMAND_VAR_OPENER, WdlTypes.RBRACE, false),
    new BracePair(WdlTypes.COMMAND_VAR_OPENER, WdlTypes.RBRACE, false),
    new BracePair(WdlTypes.LPAREN, WdlTypes.RPAREN, false),
    new BracePair(WdlTypes.LBRACE, WdlTypes.RBRACE, false),
    new BracePair(WdlTypes.LSQUARE, WdlTypes.RSQUARE, false),
    new BracePair(WdlTypes.COMMAND_DELIMITER_OPEN, WdlTypes.COMMAND_DELIMITER_CLOSE, false)
  )

  /**
    * Below text copied from PairedBraceMatcher. I chose to ignore this and just return the input. Seems to work fine...
    *
    * Returns the start offset of the code construct which owns the opening structural brace at the specified offset. For example,
    * if the opening brace belongs to an 'if' statement, returns the start offset of the 'if' statement.
    *
    * @param file the file in which brace matching is performed.
    * @param openingBraceOffset the offset of an opening structural brace.
    * @return the offset of corresponding code construct, or the same offset if not defined.
    */
  override def getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}
