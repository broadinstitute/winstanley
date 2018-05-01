package winstanley

import com.intellij.testFramework.ParsingTestCase
import winstanley.ParsingSpec._

class ParsingSpec extends ParsingTestCase("", "wdl", new WdlParserDefinition()) {

  // To add a new parsing test:
  // - Add a new WDL file into testData/parsing (eg Hello.wdl)
  // - Use PSI viewer to get a PSI breakdown of the file and copy into testData/parsing (eg Hello.txt)
  //  - NB: you might need to do some PSI whitespace tidying up around the edges
  // - Add a new test below (eg `def testHello(): Unit = doTest(true)`)
  // - Make sure the test name corresponds to the .wdl and .txt files (eg Hello.wdl + Hello.txt -> testHello)

  def testHello(): Unit = doTest(true)
  def testHello_Alias(): Unit = doTest(true)
  def testHello_version_1_0(): Unit = doTest(true)
  def testHello_draft_3(): Unit = doTest(true)
  def testInput_expr_absent(): Unit = doTest(true)
  def testInput_expr_present(): Unit = doTest(true)
  def testWorkflow_meta_sections(): Unit = doTest(true)


  override def getTestDataPath: String = parsingTestPath
  override def skipSpaces(): Boolean = false
  override def includeRanges(): Boolean = true
}

object ParsingSpec {
  val parsingTestPath = "tests/testData/parsing"
}
