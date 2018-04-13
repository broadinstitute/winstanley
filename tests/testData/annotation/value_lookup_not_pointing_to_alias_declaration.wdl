workflow hello_wf {
  call me as my_alias

  output {
    Int out = <error descr="No declaration found for 'me'">me</error>.j
  }
}

task me {
  <weak_warning descr="Non-input declarations will require immediate assignment in a future version of WDL">Int i</weak_warning>
  command {
    echo ${i}
  }

  output {
    Int j = read_int(stdout())
  }
}
