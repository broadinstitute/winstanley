# test that in 1.0, declarations without assignments are still allowed in the input block
version 1.0

workflow hello_wf {
  input {
    Int a = 1.0
  }
}
