package winstanley.formatting

import com.intellij.formatting._
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util

import com.intellij.psi.TokenType
import winstanley.psi.WdlTypes

import scala.languageFeature.postfixOps._
import scala.annotation.tailrec
import collection.JavaConverters._

class WdlBlock(@NotNull node: ASTNode,
               @Nullable wrap: Wrap,
               @Nullable alignment: Alignment,
               spacingBuilder: SpacingBuilder,
               myIndent: Indent) extends AbstractBlock(node, wrap, alignment) {

  protected def buildChildren: util.List[Block] =
    buildChildrenReversed(List(), myNode.getFirstChildNode, Indent.getNoneIndent).reverse.asJava

  @tailrec
  private def buildChildrenReversed(blocks: List[Block], child: ASTNode, indent: Indent): List[Block] = {
    if (child == null) {
      blocks
    } else {
      val wrap = Wrap.createWrap(WrapType.NONE, false)
      val alignment = Alignment.createAlignment
      if (child.getElementType == TokenType.WHITE_SPACE) {
        buildChildrenReversed(blocks, child.getTreeNext, indent)
      } else if (BLOCK_STARTING_ELEMENTS.contains(child.getElementType)) {
        val block = new WdlBlock(child, wrap, alignment, spacingBuilder, indent)
        buildChildrenReversed(block :: blocks, child.getTreeNext, Indent.getNormalIndent)
      } else if (BLOCK_CLOSING_ELEMENTS.contains(child.getElementType)) {
        val block = new WdlBlock(child, wrap, alignment, spacingBuilder, Indent.getNoneIndent)
        buildChildrenReversed(block :: blocks, child.getTreeNext, Indent.getNoneIndent)
      } else if (this.getNode.getElementType == WdlTypes.CALL_INPUT && child.getElementType == WdlTypes.MAPPING) {
        // special case to handle mappings inside call input block
        val block = new WdlBlock(child, wrap, alignment, spacingBuilder, Indent.getNormalIndent)
        buildChildrenReversed(block :: blocks, child.getTreeNext, indent)
      } else {
        val block = new WdlBlock(child, wrap, alignment, spacingBuilder, indent)
        buildChildrenReversed(block :: blocks, child.getTreeNext, indent)
      }
    }
  }

  override def getIndent: Indent = myIndent

  override def getChildAttributes(newChildIndex: Int): ChildAttributes =
    new ChildAttributes(Indent.getNormalIndent, null)

  @Nullable def getSpacing(@Nullable child1: Block, @NotNull child2: Block): Spacing =
    spacingBuilder.getSpacing(this, child1, child2)

  def isLeaf: Boolean = myNode.getFirstChildNode == null

  private val BLOCK_STARTING_ELEMENTS = Set(WdlTypes.LBRACE, WdlTypes.COMMAND_DELIMITER_OPEN)
  private val BLOCK_CLOSING_ELEMENTS = Set(WdlTypes.RBRACE, WdlTypes.COMMAND_DELIMITER_CLOSE)

}
