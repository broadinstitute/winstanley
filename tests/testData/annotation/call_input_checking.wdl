version 1.0

import "nonexistent_import.wdl" as nonexistent_import

workflow my_workflow {

    # === NO INPUTS ===

    # No inputs, none provided (correct)
    call no_inputs

    # No inputs, empty scope for input block (highlight)
    <error descr="Task does not take inputs.">call no_inputs {

    }</error>

    # No inputs, empty input list (highlight)
    # IMO: this one is a bit debatable as to whether it should highlight, since the list IS technically empty (AEN, 2018-07-17)
    <error descr="Task does not take inputs.">call no_inputs {
        input:
    }</error>

    # No inputs, full input block provided (highlight)
    <error descr="Task does not take inputs.">call no_inputs {
        input:
            a = "a",
            b = 5,
            c = false
    }</error>


    # === REQUIRED INPUTS ===

    # Required inputs provided (correct)
    call required_inputs {
         input:
            a = "a",
            b = 5,
            c = false
    }

    # Required inputs provided, reordered (correct)
    call required_inputs {
        input:
            b = 5,
            a = "a",
            c = false
    }

    # All required inputs provided, plus extraneous (highlight)
    <error descr="Unexpected inputs(s) for task: d">call required_inputs {
        input:
            a = "a",
            b = 5,
            c = false,
            d = "Zardoz"
    }</error>

    # Wrong input name (highlight; two annotations)
    <error descr="Missing required inputs(s) for task: a"><error descr="Unexpected inputs(s) for task: aa">call required_inputs {
        input:
            aa = "a",
            b = 5,
            c = false
    }</error></error>

    # No input block (highlight)
    <error descr="Task has required inputs.">call required_inputs</error>

    # Empty input scope (highlight)
    <error descr="Missing required inputs(s) for task: a, b, c">call required_inputs {

    }</error>

    # Empty input list (highlight)
    <error descr="Missing required inputs(s) for task: a, b, c">call required_inputs {
        input:
    }</error>

    # Missing inputs (highlight)
    <error descr="Missing required inputs(s) for task: b, c">call required_inputs {
        input:
            a = "a"
    }</error>


    # === DEFAULT AND OPTIONAL INPUTS ===

    # Omit optional input (correct)
    call my_task_optional {
        input:
            a = "a",
            b = 5
    }

    # Include optional input (correct)
    call my_task_optional {
        input:
            a = "a",
            b = 5,
            c = false
    }

    # Omit default input (correct)
    call my_task_default {
        input:
            a = "a",
            b = 5
    }

    # Include default input (correct)
    call my_task_default {
        input:
            a = "a",
            b = 5,
            c = false
    }

    # Omit default & optional input (correct)
    call my_task_default_and_optional {
        input:
            a = "a",
            b = 5
    }

    # Include default & optional input (correct)
    call my_task_default_and_optional {
        input:
            a = "a",
            b = 5
    }


    # === ERRATA ===

    # We do not yet validate imports (correct)
    # See https://github.com/broadinstitute/winstanley/issues/63
    call nonexistent_import.import_task {
        input:
            a = "a",
            b = 5,
            c = false
    }

    # We do not yet check types (correct)
    # See https://github.com/broadinstitute/winstanley/issues/37
    call required_inputs {
         input:
            a = false,
            b = false,
            c = false
    }
}

task required_inputs {
    runtime { docker: "dummy runtime" }

    input {
        String a
        Int b
        Boolean c
    }

    command <<<
        arbitrary junk in another language
    >>>

    output {
        Int dummy_output = 1
    }
}

task no_inputs {
    runtime { docker: "dummy runtime" }

    command <<<
        arbitrary junk in another language
    >>>

    output {
        Int dummy_output = 1
    }
}

task my_task_optional {
    runtime { docker: "dummy runtime" }

    input {
        String a
        Int b
        Boolean? c
    }

    command <<<
        arbitrary junk in another language
    >>>

    output {
        Int dummy_output = 1
    }
}

task my_task_default {
    runtime { docker: "dummy runtime" }

    input {
        String a
        Int b
        Boolean c = true
    }

    command <<<
        arbitrary junk in another language
    >>>

    output {
        Int dummy_output = 1
    }
}

task my_task_default_and_optional {
    runtime { docker: "dummy runtime" }

    input {
        String a
        Int b
        Boolean? c = true
    }

    command <<<
        arbitrary junk in another language
    >>>

    output {
        Int dummy_output = 1
    }
}

