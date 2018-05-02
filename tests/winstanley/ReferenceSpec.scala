package winstanley

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import winstanley.psi.{WdlCallAlias, WdlFullyQualifiedName}
import winstanley.psi.impl.{WdlDeclarationImpl, WdlTaskDeclarationImpl}

import scala.reflect.ClassTag

class ReferenceSpec extends LightCodeInsightFixtureTestCase {

  def testPositiveTaskReference(): Unit = referenceTest[WdlTaskDeclarationImpl]("positive_task_reference.wdl", "hello_task")

  def testPositiveVariableLookupReference(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_lookup_reference.wdl", "i")

  def testPositiveCallBlockReference(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference.wdl", "me")

  def testPositiveAliasReference(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference.wdl", "my_alias")

  def testPositiveAliasReferenceWithDecoyTask(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_decoy_task.wdl", "bar")

  def testPositiveVariableReferenceInScatter(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_reference_in_scatter.wdl", "foo")

  def testPositiveVariableReferenceInConditional(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_reference_in_conditional.wdl", "foo")

  def testPositiveVariableReferenceInNestedConditional(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_reference_in_nested_conditional.wdl", "foo")

  def testPositiveVariableReferenceConditionalInScatter(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_reference_conditional_in_scatter.wdl", "that")

  def testPositiveVariableReferenceScatterInConditional(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_reference_scatter_in_conditional.wdl", "that")

  def testPositiveVariableReferenceNestedScatterInConditional(): Unit = referenceTest[WdlDeclarationImpl]("positive_variable_reference_nested_scatter_in_conditional.wdl", "that")

  def testPositiveCallBlockReferenceInScatter(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference_in_scatter.wdl", "foo")

  def testPositiveCallBlockReferenceInConditional(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference_in_conditional.wdl", "foo")

  def testPositiveCallBlockReferenceInNestedConditional(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference_in_nested_conditional.wdl", "foo")

  def testPositiveCallBlockReferenceConditionalInScatter(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference_conditional_in_scatter.wdl", "foo")

  def testPositiveCallBlockReferenceScatterInConditional(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference_scatter_in_conditional.wdl", "foo")

  def testPositiveCallBlockReferenceNestedScatterInConditional(): Unit = referenceTest[WdlFullyQualifiedName]("positive_call_block_reference_nested_scatter_in_conditional.wdl", "foo")

  def testPositiveAliasReferenceInConditional(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_in_conditional.wdl", "oof")

  def testPositiveAliasReferenceInScatter(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_in_scatter.wdl", "oof")

  def testPositiveAliasReferenceInNestedConditional(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_in_nested_conditional.wdl", "oof")

  def testPositiveAliasReferenceConditionalInScatter(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_conditional_in_scatter.wdl", "oof")

  def testPositiveAliasReferenceScatterInConditional(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_scatter_in_conditional.wdl", "oof")

  def testPositiveAliasReferenceNestedScatterInConditional(): Unit = referenceTest[WdlCallAlias]("positive_alias_reference_nested_scatter_in_conditional.wdl", "oof")

  /**
    * Various cases of
    * @param path The path to the test wdl
    * @param expectedReferenceText The name of the expected reference
    * @tparam ExpectedReferenceType The type of the expected reference
    * @param ct implicitly supplied, prevents type erasure for the case match on the generic type
    */
  private def referenceTest[ExpectedReferenceType](path: String, expectedReferenceText: String)(implicit ct: ClassTag[ExpectedReferenceType]): Unit = {
    myFixture.configureByFile(path)
    val resolvedReference = myFixture.getReferenceAtCaretPositionWithAssertion(path).resolve()
    resolvedReference.getParent match {
      case _: ExpectedReferenceType => // good! (this is why we need the ct param)
      case other => assert(assertion = false, s"Expected ${scala.reflect.classTag[ExpectedReferenceType].runtimeClass.getSimpleName} but got ${other.getClass.getSimpleName}")
    }
    assert(resolvedReference.getText == expectedReferenceText)
  }

  override def setUp(): Unit = super.setUp()
  override def tearDown(): Unit = super.tearDown()
  override def getTestDataPath: String = "tests/testData/reference/"
}
