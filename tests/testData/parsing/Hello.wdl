workflow hello_wf {
  call hello_task
}

task hello_task {
  Int i
  command {
    echo ${i}
  }

  output {
    Int j = read_int(stdout())
  }
}
