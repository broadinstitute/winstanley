# Exists to make sure we do NOT highlight bad inputs in draft-2

workflow my_workflow {

    # Duplicate input (no annotation; would be an error highlight in 1.0)
    call required_inputs {
        input:
            a = "a",
            a = "a",
            b = 5,
            c = false
    }

    # Unsupplied inputs (no annotation; would be a warning highlight in 1.0)
    call required_inputs {
        input:
            a = "a"
    }
}

task required_inputs {
    runtime { docker: "dummy runtime" }

    String a
    Int b
    Boolean c

    command <<<
        arbitrary junk in another language
    >>>

    output {
        Int dummy_output = 1
    }
}
