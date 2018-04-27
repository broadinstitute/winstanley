version draft-3

task <weak_warning descr="Non-portable task section: add a runtime section specifying a docker image">no_runtime_section</weak_warning> {
  command { }
  output {
    String s = read_string(stdout())
  }
}
