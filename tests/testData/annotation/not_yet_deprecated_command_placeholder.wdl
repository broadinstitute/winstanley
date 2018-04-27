# No annotations please!

task deprecated_command_placeholder {
  command {
    echo ${5}
  }
  runtime {
    docker: "ubuntu:latest"
  }
  output {
    String s = read_string(stdout())
  }
}
