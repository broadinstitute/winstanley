version 1.0
# test that in 1.0, declarations without assignments are allowed in the input block
# (and that '1.0' is not a reserved sequence globally)

workflow hello_wf {
  input {
    Int a = 1.0
  }
}
