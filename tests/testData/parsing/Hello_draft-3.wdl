# test that in draft-3, declarations without assignments are still allowed in the input block
version draft-3
workflow hello_wf {
  input {
    Int a
  }
}
