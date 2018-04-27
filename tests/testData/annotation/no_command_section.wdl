version draft-3

task <error descr="No command specified for task">no_command_section</error> {
  runtime {
    docker: "ubuntu:latest"
  }
  output {
    String s = read_string(stdout())
  }
}
