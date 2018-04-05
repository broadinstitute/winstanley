package winstanley

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import winstanley.psi.WdlTypes
import org.jetbrains.annotations.NotNull
import com.intellij.openapi.editor.colors.TextAttributesKey._

object WdlSyntaxHighlighter {

  val mappings = Map (
    WdlTypes.QUOTE -> DefaultLanguageHighlighterColors.STRING,
    WdlTypes.QUOTE_STRING_CHAR -> DefaultLanguageHighlighterColors.STRING,
    WdlTypes.ESCAPE_SEQUENCE -> DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE,
    WdlTypes.COMMAND_CHAR -> DefaultLanguageHighlighterColors.CONSTANT,
    WdlTypes.BOOLEAN -> DefaultLanguageHighlighterColors.CONSTANT,
    WdlTypes.NUMBER -> DefaultLanguageHighlighterColors.NUMBER,

    WdlTypes.PRIMITIVE_TYPE -> DefaultLanguageHighlighterColors.METADATA,
    WdlTypes.OBJECT_TYPE -> DefaultLanguageHighlighterColors.METADATA,
    WdlTypes.ARRAY_TYPE -> DefaultLanguageHighlighterColors.METADATA,
    WdlTypes.MAP_TYPE -> DefaultLanguageHighlighterColors.METADATA,
    WdlTypes.PAIR_TYPE -> DefaultLanguageHighlighterColors.METADATA,

    WdlTypes.LBRACE -> DefaultLanguageHighlighterColors.BRACES,
    WdlTypes.RBRACE -> DefaultLanguageHighlighterColors.BRACES,
    WdlTypes.LPAREN -> DefaultLanguageHighlighterColors.PARENTHESES,
    WdlTypes.RPAREN -> DefaultLanguageHighlighterColors.PARENTHESES,
    WdlTypes.COLON -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.EQUAL -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.DOT -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.QMARK -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.PLUS -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.DASH -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.ASTERISK -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.SLASH -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.PERCENT -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.LOGICAL_NOT -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.DOUBLE_EQUAL -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.NOT_EQUAL -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.DOUBLE_AMPERSAND -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.DOUBLE_PIPE -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.LESS_THAN -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.LESS_EQUAL -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.MORE_THAN -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.MORE_EQUAL -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.NOT_EQUAL -> DefaultLanguageHighlighterColors.OPERATION_SIGN,
    WdlTypes.COMMA -> DefaultLanguageHighlighterColors.COMMA,
    WdlTypes.COMMENT -> DefaultLanguageHighlighterColors.LINE_COMMENT,
    WdlTypes.IMPORT -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.WORKFLOW -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.WORKFLOW_IDENTIFIER_DECL -> DefaultLanguageHighlighterColors.FUNCTION_DECLARATION,
    WdlTypes.CALL -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.AS -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.INPUT -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.OUTPUT -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.WHILE -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.IF -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.THEN -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.ELSE -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.SCATTER -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.IN -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.TASK -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.TASK_IDENTIFIER_DECL -> DefaultLanguageHighlighterColors.FUNCTION_DECLARATION,
    WdlTypes.COMMAND -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.COMMAND_ATTR_SEP -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.COMMAND_ATTR_DEFAULT -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.COMMAND_ATTR_TRUE -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.COMMAND_ATTR_FALSE -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.RUNTIME -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.PARAMETER_META -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.META -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.OBJECT -> DefaultLanguageHighlighterColors.KEYWORD,

    WdlTypes.VERSION -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.VERSION_IDENTIFIER -> DefaultLanguageHighlighterColors.CONSTANT,
    WdlTypes.STRUCT -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.ALIAS -> DefaultLanguageHighlighterColors.KEYWORD,
    WdlTypes.STRUCT_IDENTIFIER_DECL -> DefaultLanguageHighlighterColors.METADATA
  )

}

class WdlSyntaxHighlighter extends SyntaxHighlighterBase {
  import WdlSyntaxHighlighter._
  @NotNull
  override def getHighlightingLexer: Lexer = new WdlLexerAdapter()

  @NotNull
  override def getTokenHighlights(tokenType: IElementType): Array[TextAttributesKey] = {
    mappings find {
      case (token, colors) =>
        tokenType.equals(token)
    } map { _._2} match {
      case Some(x) => Seq(createTextAttributesKey(tokenType.toString, x)).toArray
      case None => Array.empty[TextAttributesKey]
    }
  }
}
