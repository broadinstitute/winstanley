workflow hello_wf {
  call hello_task
}

task hello_task {
  <weak_warning descr="Non-input declarations will require immediate assignment in a future version of WDL">Int i</weak_warning>
  command {
    echo ${i}
  }

  output {
    Int j = read_int(stdout())
  }
}
