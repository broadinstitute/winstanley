package winstanley

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import winstanley.parser.WdlParser
import winstanley.psi.WdlFile
import winstanley.psi.WdlTypes
import org.jetbrains.annotations.NotNull

object WdlParserDefinition {
  val WHITE_SPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
  val COMMENTS: TokenSet = TokenSet.create(WdlTypes.COMMENT)
  val FILE: IFileElementType = new IFileElementType(Language.findInstance[WdlLanguage](classOf[WdlLanguage]))
}

class WdlParserDefinition extends ParserDefinition {

  @NotNull
  override def createLexer(project: Project): Lexer = new WdlLexerAdapter()

  @NotNull
  override def getWhitespaceTokens: TokenSet = WdlParserDefinition.WHITE_SPACES

  @NotNull
  override def getCommentTokens: TokenSet = WdlParserDefinition.COMMENTS

  @NotNull
  override def getStringLiteralElements: TokenSet = TokenSet.EMPTY

  @NotNull
  override def createParser(project: Project): PsiParser = new WdlParser()

  override def getFileNodeType: IFileElementType = WdlParserDefinition.FILE

  override def createFile(viewProvider: FileViewProvider): PsiFile = new WdlFile(viewProvider)

  override def spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): SpaceRequirements = SpaceRequirements.MAY

  @NotNull
  override def createElement(node: ASTNode): PsiElement = WdlTypes.Factory.createElement(node)
}
