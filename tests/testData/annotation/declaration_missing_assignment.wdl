version 1.0
workflow foo {
  <error descr="Immediate assignment required for non-input declaration: do you need to add an 'input { }' section? [WDL 1.0]">Int i</error>
}
