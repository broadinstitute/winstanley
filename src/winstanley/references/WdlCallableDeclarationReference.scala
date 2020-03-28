package winstanley.references

import com.intellij.openapi.util.TextRange
import com.intellij.psi.{PsiElement, PsiReferenceBase}
import org.jetbrains.annotations.Nullable
import winstanley.psi.WdlCallableLookup
import winstanley.structure.WdlImplicits._

/**
  * Holds a PSI element (eg `call taskName`) that references a callable task.
  * Defines methods to find and return the named task element for the task being called.
  * todo implement for workflow
  */
final case class WdlCallableDeclarationReference(value: WdlCallableLookup) extends PsiReferenceBase[PsiElement](value, value.getTextRange) {

  /**
    * Returns the element which is the target of the reference !!! OR NULL IF NOT FOUND !!!
    *
    * @return the target element, or null if it was not possible to resolve the reference to a valid target.
    * @see PsiPolyVariantReference#multiResolve(boolean)
    */
  @Nullable
  override def resolve(): PsiElement = {
    value.findTaskDeclarationsInScope.find(d => value.getCalledIdentifier == d.getNameIdentifier.getText).map(_.getNameIdentifier).orNull
  }

  /**
    * Returns the array of String, PsiElement and/or LookupElement
    * instances representing all identifiers that are visible at the location of the reference. The contents
    * of the returned array is used to build the lookup list for basic code completion. (The list
    * of visible identifiers may not be filtered by the completion prefix string - the
    * filtering is performed later by IDEA core.)
    *
    * @return the array of available identifiers.
    */
  override def getVariants: Array[AnyRef] = Array.empty[AnyRef]

  /**
    * This override is required to make reference-lookup work.
    *
    * It needs a relative range within the PsiElement 'value' to count as the reference,
    * which in this case is the entire 'WdlCallableLookup' element.
    */
  override def getRangeInElement: TextRange = new TextRange(0, value.getTextLength)

}
