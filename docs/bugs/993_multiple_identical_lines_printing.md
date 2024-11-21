# Avoid multiple identical lines printing

Related to [#993](https://github.com/Flank/flank/issues/993)

Sometimes Flank prints identical lines multiple times. This bug occurs rarely, there is no clear way to reproduce or force this by code changes.

What was checked:

- [X] launched flank with a different configuration, different count of matrices
- [X] on ```PollMatrices.kt```  
  
  ```kotlin
  onEach {
        printMatrixStatus(it)
    }
  ```

It always executes on the same thread so it is not a concurrency issue

- [X] on ```ExecutionStatusPrinter.kt``` -> ```MultiLinePrinter``` try to force remove less lines than ```output.size``` but in this case, this line does not update status, so the behaviour is different than on screen
  
- [X] on ```ExecutionStatusPrinter.kt``` -> ```MultiLinePrinter``` try to force add to output two same ExecutionStatus but no effect

- [X] on ```GcTestMatrix.kt``` -> ```refresh()``` try to add testSpecification with same id but without effect
