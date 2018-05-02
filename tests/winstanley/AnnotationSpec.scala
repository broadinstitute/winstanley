package winstanley

import java.io.{File, FilenameFilter}
import winstanley.AnnotationSpec._
import winstanley.ParsingSpec._
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
    val parsingTestWdls = new File(parsingTestPath).listFiles(WdlFilenameFilter)
    parsingTestWdls foreach { wdl => annotationTest(wdl.getAbsolutePath) }
  }

  def testDeclarationMissingAssignment(): Unit = annotationTest("declaration_missing_assignment.wdl")
  def testOutputMissingDeclaration(): Unit = annotationTest("output_missing_declaration.wdl")
  def testMissingTaskDeclaration(): Unit = annotationTest("missing_task_declaration.wdl")
  def testMissingAliasDeclaration(): Unit = annotationTest("missing_alias_declaration.wdl")
  def testLookupNotPointingToAliasDeclaration(): Unit = annotationTest("value_lookup_not_pointing_to_alias_declaration.wdl")
  def testImportedTaskNotAnnotated(): Unit = annotationTest("import_sub_wf.wdl")
  def testDeprecatedCommandPlaceholder(): Unit = annotationTest("deprecated_command_placeholder.wdl")
  def testNotYetDeprecatedCommandPlaceholder(): Unit = annotationTest("not_yet_deprecated_command_placeholder.wdl")
  def testNoCommandSection(): Unit = annotationTest("no_command_section.wdl")
  def testNoRuntimeSection(): Unit = annotationTest("no_runtime_section.wdl")
  def testNoDockerAttribute(): Unit = annotationTest("no_docker_attribute.wdl")
  def testDraft2WildcardOutputs(): Unit = annotationTest("draft_2_wildcards.wdl")
  def testVersion10WildcardOutputs(): Unit = annotationTest("version_1_0_wildcards.wdl")

  private def annotationTest(path: String): Unit = {
    myFixture.configureByFile(path)

    // Since they aren't usefully named, these booleans represent:
    // checkWarnings, checkInfos, checkWeakWarnings, ignoreExtraHighlighting
    myFixture.checkHighlighting(true, true, true, false)
  }

  override def setUp(): Unit = super.setUp()
  override def tearDown(): Unit = super.tearDown()
  override def getTestDataPath: String = "tests/testData/annotation/"
}

object AnnotationSpec {
  object WdlFilenameFilter extends FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(".wdl")
  }

  val annotationTestPath = "tests/testData/annotation/"
}
