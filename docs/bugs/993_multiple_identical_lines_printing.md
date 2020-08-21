# Avoid multiple identical lines printing

Related to [#993](https://github.com/Flank/flank/issues/993)

Sometimes Flank printing identical lines multiple times. This bug occur rarely, there is no clear way to reproduce or force this by code changes.

What was checked:

- [X] launched flank with a different configuration, different count of matrices
- [X] on ```PollMatrices.kt```  
  
  ```kotlin
  onEach {
        printMatrixStatus(it)
    }
  ```

  always executing on same thread so it's no concurrency issue

- [X] on ```ExecutionStatusPrinter.kt``` -> ```MultiLinePrinter``` try to force remove less lines than ```output.size``` but in this case, this line not update status, so the behaviour is different than on screen
  
- [X] on ```ExecutionStatusPrinter.kt``` -> ```MultiLinePrinter``` try to force add to output two same ExecutionStatus but no effect

- [X] on ```GcTestMatrix.kt``` -> ```refresh()``` try to add testSpecyfication with same id but without effect
