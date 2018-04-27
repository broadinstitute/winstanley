workflow hello_wf {
  call hello_task as hello_alias
  output {
    Int out = hello_alias.j
  }
}

task hello_task {
  Int i
  command {
    echo ${i}
  }

  output {
    Int j = read_int(stdout())
  }
  runtime {
    docker: "ubuntu:latest"
  }
}
