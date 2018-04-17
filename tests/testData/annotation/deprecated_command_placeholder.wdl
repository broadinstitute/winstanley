version draft-3

task deprecated_command_placeholder {
  command {
    echo <warning descr="Deprecated placeholder style: Use ~{ ... } from WDL draft 3 onwards to match 'command <<<' section placeholders">${5}</warning>
    echo ~{"no warning"}
  }
}

task command_placeholder {
  command <<<
    echo ${no warning (we're in bash now)}
    echo ~{"no warning"}
  >>>
}
