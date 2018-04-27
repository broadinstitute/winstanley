workflow hello_wf {
  call me as my_alias

  output {
    Int out = <error descr="No declaration found for 'me'">me</error>.j
  }
}

task me {
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
