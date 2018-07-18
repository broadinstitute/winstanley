version 1.0

import "nonexistent_import.wdl" as nonexistent_import

workflow my_workflow {

    # === NO INPUTS ===

    # No inputs, none provided (correct)
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

    # All required inputs provided, plus extraneous (error highlight)
    <error descr="Unexpected inputs(s) for task: d">call required_inputs {
        input:
            a = "a",
            b = 5,
            c = false,
            d = "Zardoz"
    }</error>

    # Wrong input name (error highlight; two annotations)
    <error descr="Unexpected inputs(s) for task: aa">call required_inputs {
        input:
            aa = "a",
            b = 5,
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

    # Missing inputs (warning highlight)
    <weak_warning descr="Unsupplied input(s) 'b', 'c' must be assigned here or, if this is the root workflow, provided in the inputs JSON.">call required_inputs {
        input:
            a = "a"
    }</weak_warning>


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

task <weak_warning descr="Non-portable task section: add a runtime section specifying a docker image">required_inputs</weak_warning> {
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

