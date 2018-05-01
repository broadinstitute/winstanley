workflow wildcard_outputs {
  call my_task as a
  call my_task as b

  output {
    <weak_warning descr="Declaration style outputs will be required in a later version of WDL">f.*</weak_warning>
    <weak_warning descr="Declaration style outputs will be required in a later version of WDL">b.v</weak_warning>
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
