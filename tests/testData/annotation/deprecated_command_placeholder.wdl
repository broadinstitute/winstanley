version 1.0

task deprecated_command_placeholder {
  command {
    echo <weak_warning descr="Deprecated placeholder style: Use ~{ ... } for WDL 1.0 (draft-3) onwards to match 'command <<<' section placeholders">${5}</weak_warning>
    echo ~{"no warning"}
  }
  runtime {
    docker: "ubuntu:latest"
  }
  output {
    String s = read_string(stdout())
  }
}

task command_placeholder {
  command <<<
    echo ${no warning (we're in bash now)}
    echo ~{"no warning"}
  >>>
  runtime {
    docker: "ubuntu:latest"
  }
  output {
    String s = read_string(stdout())
  }
}
