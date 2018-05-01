version draft-3

workflow meta_sections {
  meta {
    description: "blah"
    array: [ 0, 1, 2 ]
    obj: {
      field1: 5,
      field2: "field2"
    }
  }

  parameter_meta {
    all_good: "it's all good!"
  }

  input {
    String all_good
  }
}
