package winstanley.formatting

import com.intellij.formatting.{Block, Spacing, SpacingBuilder}
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.annotations._
import winstanley.psi.WdlTypes

import scala.util.Try

class WdlSpacingBuilder(@NotNull codeStyleSettings: CodeStyleSettings,
                        @NotNull language: Language) extends SpacingBuilder(codeStyleSettings, language) {

  override def getSpacing(parent: Block, child1: Block, child2: Block): Spacing = {
    val defaultSpacing = super.getSpacing(parent, child1, child2)
    val isInCommandVar = Try(parent.asInstanceOf[WdlBlock]).toOption.map(_.getNode.getElementType == WdlTypes.COMMAND_VAR)

    // we need no formatting for a variable placeholder inside a command block
    if (isInCommandVar.contains(true)) {
      defaultSpacing
    } else {

      val leftOption = Option(child1).map(_.asInstanceOf[WdlBlock])
      val right = child2.asInstanceOf[WdlBlock]

      val spacingOpt = leftOption map { left =>
        val needSpace = ELEMENTS_REQUIRING_SPACE_IN_FRONT.contains(right.getNode.getElementType)
        val needBlankLine =
          ELEMENTS_REQUIRING_EMPTY_LINE_IN_FRONT.contains(right.getNode.getElementType) ||
            ELEMENTS_REQUIRING_EMPTY_LINE_AFTER.contains(left.getNode.getElementType)
        val needNewLine =
          ELEMENTS_REQUIRING_NEW_LINE_IN_FRONT.contains(right.getNode.getElementType) ||
            ELEMENTS_REQUIRING_NEW_LINE_AFTER.contains(left.getNode.getElementType)

        if (needSpace) {
          ONE_SPACE
        } else if (needBlankLine) {
          NO_SPACES_NEW_LINE_WITH_BLANK_LINE
        } else if (needNewLine) {
          NO_SPACES_NEW_LINE
        } else {
          defaultSpacing
        }
      }

      spacingOpt.getOrElse {
        defaultSpacing
      }
    }
  }

  private val ONE_SPACE = Spacing.createSpacing(1, 1, 0, false, 0)
  private val NO_SPACES_NEW_LINE = Spacing.createSpacing(0, 0, 1, false, 1)
  private val NO_SPACES_NEW_LINE_WITH_BLANK_LINE = Spacing.createSpacing(0, 0, 2, false, 1)

  private val ELEMENTS_REQUIRING_SPACE_IN_FRONT = Set(WdlTypes.LBRACE, WdlTypes.COMMAND_DELIMITER_OPEN)

  private val ELEMENTS_REQUIRING_NEW_LINE_IN_FRONT =
    Set(
      WdlTypes.IMPORT_STMT,
      WdlTypes.RBRACE,
      WdlTypes.COMMAND_DELIMITER_CLOSE,
      WdlTypes.CALL_BLOCK,
      WdlTypes.WF_BODY_ELEMENT,
      WdlTypes.WF_OUTPUT,
      WdlTypes.OUTPUT_KV,
      WdlTypes.DECLARATION,
      WdlTypes.STRUCT_ENTRY,
      WdlTypes.TASK_SECTION,
      WdlTypes.MAPPING,
    )

  private val ELEMENTS_REQUIRING_EMPTY_LINE_IN_FRONT =
    Set(
      WdlTypes.WORKFLOW_BLOCK,
      WdlTypes.TASK_BLOCK,
      WdlTypes.STRUCT_BLOCK,
    )

  private val ELEMENTS_REQUIRING_NEW_LINE_AFTER =
    Set(
      WdlTypes.LBRACE,
      WdlTypes.COMMAND_DELIMITER_OPEN,
      WdlTypes.TASK_SECTION,
      WdlTypes.KV,
    )

  private val ELEMENTS_REQUIRING_EMPTY_LINE_AFTER = Set(WdlTypes.VERSION_IDENTIFIER)

}
