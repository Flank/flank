# Outcome incorrectly set by Flank [#914](https://github.com/Flank/flank/issues/914)

##### Changelog
|Date|Who?|Action|
|---|---|---|
|31th July 2020|[pawelpasterz](https://github.com/pawelpasterz)|created|
|1st October 2020|[adamfilipow92](https://github.com/adamfilipow92)|update|
|   |   |   |

### Description
```
[task 2020-07-23T20:29:15.152Z] ┌─────────┬──────────────────────┬──────────────────────────────────────────┐
[task 2020-07-23T20:29:15.153Z] │ OUTCOME │      MATRIX ID       │               TEST DETAILS               │
[task 2020-07-23T20:29:15.153Z] ├─────────┼──────────────────────┼──────────────────────────────────────────┤
[task 2020-07-23T20:29:15.153Z] │  flaky  │ matrix-1vj2pt9wih182 │ 1 test cases failed, 106 passed, 2 flaky │
[task 2020-07-23T20:29:15.153Z] └─────────┴──────────────────────┴──────────────────────────────────────────┘
```
FTL confirmed the matrix outcome is a failure. Flank reported flaky.

### Steps to reproduce
So far no reliable way to reproduce it (with 10/10 rate) found. One can simulate error with the test:
```
SavedMatrixTest#`savedMatrix should have failed outcome when at least one test is failed and the last one is flaky`
```

Due to the incorrectly implemented logic regarding the status update, there were cases when flaky tests obscure failed results.
The following must occur:
1. The matrix needs to have both flaky and failed tests (at least one of each)
2. TestExecutions need to be returned in a specific order:
    1. Failed execution must be consumed by flank before flaky one
    2. The following flaky execution must be actually failed but the outcome of all re-runs is success
3. Then flank does what is the following:
    1. The outcome is set to `failure` when failed test/shards/executions are processed
    ```kotlin
   private fun Outcome?.updateOutcome(
       when {
           ...
           else -> this?.summary  // at this point SavedMatrix.outcome == success, flank changes it to failure
           ...
   })
   ```
    2. When flank reaches case where step summary is `failure` but execution is `success` it sets `SavedMatrix` outcome to flaky
    ```kotlin
   updateOutcome(flakyOutcome = it.step.outcome?.summary != this?.summary) // flakyOutcome == true 
    ```
    3. Due to incorrect order in `when` condition `failure` check is never reached 
    ```kotlin
   private fun Outcome?.updateOutcome(
           flakyOutcome: Boolean
       ) {
           outcome = when {
               flakyOutcome -> flaky 
               // flank should escape here with failure status persisted, but since flakyOutcome == true SavedMatrix.outcome is changed to flaky
               outcome == failure || outcome == inconclusive -> return 
               outcome == flaky -> this?.summary?.takeIf { it == failure || it == inconclusive }
               else -> this?.summary
           } ?: outcome
       }
    ```
    
### Proposed fix
1. Change order in when condition to always check for `failure` first
    ```kotlin
   private fun Outcome?.updateOutcome(
           flakyOutcome: Boolean
       ) {
           outcome = when {
               outcome == failure || outcome == inconclusive -> return // escape when failure/inconclusive outcome is set
               flakyOutcome -> flaky 
               outcome == flaky -> this?.summary?.takeIf { it == failure || it == inconclusive }
               else -> this?.summary
           } ?: outcome
       }
    ```
2. The test below reflects potential but rare behavior.

    ```kotlin
       @Test
       fun `savedMatrix should have flaky outcome when at least one test is flaky`() {
           val expectedOutcome = "flaky"
           val successStepExecution = createStepExecution(1) // success
           // https://github.com/Flank/flank/issues/918
           // This test covers edge case where summary for both step and execution is null and outcome of
           // saved matrix was not changed and is set to success
           val malformed = createStepExecution(stepId = -666, executionId = -666) // flaky
   
           // below order in the list matters!
           val executions = listOf(
               successStepExecution,
               successStepExecution,
               malformed
           )
   
           val testMatrix = testMatrix().apply {
               testMatrixId = "123"
               state = FINISHED
               resultStorage = createResultsStorage()
               testExecutions = executions
           }
   
           val savedMatrix = createSavedMatrix(testMatrix)
   
           assertEquals(expectedOutcome, savedMatrix.outcome)
       }
   ```

3. This issue is not deterministic and really difficult to reproduce by manual tests. The community not reporting this issue for some time. 
