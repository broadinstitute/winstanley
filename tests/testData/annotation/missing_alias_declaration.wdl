workflow hello_wf {
  call me

  output {
    Int out = <error descr="No declaration found for 'my_alias'">my_alias</error>.j
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
}
