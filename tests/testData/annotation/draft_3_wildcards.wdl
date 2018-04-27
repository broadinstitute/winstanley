version draft-3

workflow wildcard_outputs {
  call my_task as a
  call my_task as b

  output {
    <error descr="Declaration style outputs are required in WDL draft 3 and later">f.*</error>
    <error descr="Declaration style outputs are required in WDL draft 3 and later">b.v</error>
  }
}

task my_task {
  command { }
  runtime { docker: "ubuntu: latest" }
  output {
    Int i = 1
    Int v = 5
    String s = "ess"
  }
}
