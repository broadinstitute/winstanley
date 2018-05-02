version 1.0

task no_docker_attribute {
  command { }
  <weak_warning descr="Non-portable runtime section: specify a docker image">runtime</weak_warning> {
    cpu: 4
  }
  output {
    String s = read_string(stdout())
  }
}
