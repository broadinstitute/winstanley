version draft-3

task deprecated_command_placeholder {
  command {
    echo <weak_warning descr="Deprecated placeholder style: Use ~{ ... } from WDL draft 3 onwards to match 'command <<<' section placeholders">${5}</weak_warning>
    echo ~{"no warning"}
  }
  runtime {
    docker: "ubuntu:latest"
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
}
