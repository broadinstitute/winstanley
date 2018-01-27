package winstanley

import java.io.{File, FilenameFilter}
import winstanley.AnnotationSpec._
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

//
// If you have trouble running these tests (eg something like cannot find Class .../netty/io/... then make sure the
// IntelliJ/lib/ directory is included in its entirety in the project class path)
//
class AnnotationSpec extends LightCodeInsightFixtureTestCase {

  /**
    * Run over the parsing WDLs and make sure they don't contain any annotations
    */
  def testNoAnnotation(): Unit = {
    val parsingTestWdls = new File(ParsingSpec.parsingTestPath).listFiles(WdlFilenameFilter)
    parsingTestWdls foreach { wdl => annotationTest(wdl.getAbsolutePath) }
  }

  def testOutputMissingDeclaration(): Unit = annotationTest("output_missing_declaration.wdl")

  def testMissingTaskDeclaration(): Unit = annotationTest("missing_task_declaration.wdl")

  private def annotationTest(path: String): Unit = {
    myFixture.configureByFile(path)

    // Since they aren't usefully named, these booleans represent:
    // checkWarnings, checkInfos, checkWeakWarnings, ignoreExtraHighlighting
    myFixture.checkHighlighting(true, true, true, false)
  }

  override def setUp(): Unit = super.setUp()
  override def getTestDataPath: String = "tests/testData/annotation/"
}

object AnnotationSpec {
  object WdlFilenameFilter extends FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(".wdl")
  }

  val annotationTestPath = "tests/testData/annotation/"
}
