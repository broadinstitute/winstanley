version 1.0

import "nonexistent_import.wdl" as nonexistent_import

workflow my_workflow {

    # === NO INPUTS ===

    # No inputs, none provided (no annotation)
    call no_inputs

    # No inputs, empty scope for input block (error highlight)
    <error descr="Task does not take inputs.">call no_inputs {

    }</error>

    # No inputs, empty input list (error highlight)
    # IMO: this one is a bit debatable as to whether it should highlight, since the list IS technically empty (AEN, 2018-07-17)
    <error descr="Task does not take inputs.">call no_inputs {
        input:
    }</error>

    # No inputs, full input block provided (error highlight)
    <error descr="Task does not take inputs.">call no_inputs {
        input:
            a = "a",
            b = 5,
            c = false
    }</error>


    # === REQUIRED INPUTS ===

    # Required inputs provided (no annotation)
    call required_inputs {
         input:
            a = "a",
            b = 5,
            c = false
    }

    # Required inputs provided, reordered (no annotation)
    call required_inputs {
        input:
            b = 5,
            a = "a",
            c = false
    }

    # All required inputs provided, plus one extraneous (error highlight)
    <error descr="Unexpected inputs(s) for task: d">call required_inputs {
        input:
            a = "a",
            b = 5,
            c = false,
            d = "Zardoz"
    }</error>

    # Wrong input name (error highlight; trumps unsupplied input warning)
    <error descr="Unexpected inputs(s) for task: aa">call required_inputs {
        input:
            aa = "a",
            b = 5,
            c = false
    }</error>

    # Duplicate input (error highlight)
    <error descr="Repeated input 'a'">call required_inputs {
        input:
            a = "a",
            a = "a",
            b = 5,
            c = false
    }</error>

    # TWO duplicate inputs (two error highlights)
    <error descr="Repeated input 'a'"><error descr="Repeated input 'b'">call required_inputs {
        input:
            a = "a",
            a = "a",
            b = 5,
            b = 5,
            c = false
    }</error></error>

    # Triplicate input (error highlight)
    <error descr="Repeated input 'a'">call required_inputs {
        input:
            a = "a",
            a = "a",
            a = "a",
            b = 5,
            c = false
    }</error>

    # Duplicate input and unsupplied input (error highlight; trumps unsupplied input warning)
    <error descr="Repeated input 'a'">call required_inputs {
        input:
            a = "a",
            a = 5,
            c = false
    }</error>

    # No input block (warning highlight)
    <weak_warning descr="Unsupplied input(s) 'a', 'b', 'c' must be assigned here or, if this is the root workflow, provided in the inputs JSON.">call required_inputs</weak_warning>

    # Empty input scope (warning highlight)
    <weak_warning descr="Unsupplied input(s) 'a', 'b', 'c' must be assigned here or, if this is the root workflow, provided in the inputs JSON.">call required_inputs {

    }</weak_warning>

    # Empty input list (warning highlight)
    <weak_warning descr="Unsupplied input(s) 'a', 'b', 'c' must be assigned here or, if this is the root workflow, provided in the inputs JSON.">call required_inputs {
        input:
    }</weak_warning>

    # Unsupplied inputs (warning highlight)
    <weak_warning descr="Unsupplied input(s) 'b', 'c' must be assigned here or, if this is the root workflow, provided in the inputs JSON.">call required_inputs {
        input:
            a = "a"
    }</weak_warning>


    # === DEFAULT AND OPTIONAL INPUTS ===

    # Omit optional input (no annotation)
    call my_task_optional {
        input:
            a = "a",
            b = 5
    }

    # Include optional input (no annotation)
    call my_task_optional {
        input:
            a = "a",
            b = 5,
            c = false
    }

    # Omit default input (no annotation)
    call my_task_default {
        input:
            a = "a",
            b = 5
    }

    # Include default input (no annotation)
    call my_task_default {
        input:
            a = "a",
            b = 5,
            c = false
    }

    # Omit default & optional input (no annotation)
    call my_task_default_and_optional {
        input:
            a = "a",
            b = 5
    }

    # Include default & optional input (no annotation)
    call my_task_default_and_optional {
        input:
            a = "a",
            b = 5
    }


    # === ERRATA ===

    # We do not yet validate imports (no annotation)
    # See https://github.com/broadinstitute/winstanley/issues/63
    call nonexistent_import.import_task {
        input:
            a = "a",
            b = 5,
            c = false
    }

    # We do not yet check types (no annotation)
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
