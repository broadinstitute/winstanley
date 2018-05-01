import "x.wdl" as x_wdl

workflow foo {
  call x_wdl.sub_wf as aliased

  output {
    Int x = aliased.x_out
  }
}
