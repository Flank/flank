# Formatted summary output 
```
┌─────────┬──────────────────────┬─────────────────────┐
│ OUTCOME │      MATRIX ID       │    TEST DETAILS     │
├─────────┼──────────────────────┼─────────────────────┤
│ success │ matrix-1z85qtvdnvb0l │ 4 test cases passed │
└─────────┴──────────────────────┴─────────────────────┘
```


## User scenario
As a user I want to see finely formatted summary result at the end of execution output.

## Motivation
Gcloud prints summary output in the table. It looks nice and is readable. Why we wouldn't have same in flank?

## Possible outputs
Numbers are representing `OUTCOME` column, points are representing `TEST DETAILS` column.
1. `success | flaky`
    * `${1} test cases passed | ${2} skipped | ${3} flakes | (Native crash) | ---`
2. `failure`
    * `${1} test cases failed | ${2} errors | ${3} passed | ${4} skipped |  ${4} flakes  | (Native crash)`
    * `Application crashed | (Native crash)`
    * `Test timed out | (Native crash)`
    * `App failed to install | (Native crash)`
    * `Unknown failure | (Native crash)`
3. `inconclusive`
    * `Infrastructure failure`
    * `Test run aborted by user`
    * `Unknown reason`
4. `skipped`
    * `Incompatible device/OS combination`
    * `App does not support the device architecture`
    * `App does not support the OS version`
    * `Unknown reason`

## Implementation details

### Outcome calculation
It should be mentioned there are some crucial differences how flank and gcloud calculates outcome value.

Gcloud is using following API calls
1. `self._client.projects_histories_executions_environments.List(request)`
2. `self._client.projects_histories_executions_steps.List(request)`

The first one is default, but if returns any `environment` without `environmentResult.outcome`, the second one will be used to obtain `steps`. 
Both `environemnts` and `steps` can provide `outcome`. The difference between them is the `steps` returns `success` event if tests are `flaky`.
Currently, we don't know why `self._client.projects_histories_executions_environments.List(request)` may return empty `environmentResult.outcome`.

In difference to gcloud flank uses 3 api call to obtain necessary data
1. `TestMatrix` - `GcTesting.get.projects().testMatrices().get(projectId, testMatrixId)`
2. `Step` - `toolsResults.projects().histories().executions().steps().get(projectId, historyId, executionId, stepId)`
3. `ListTestCasesResponse` - `toolsResults.projects().histories().executions().steps().testCases().get(projectId, historyId, executionId, stepId)`

`TestMatrix` from first call provides `ToolResultsStep` through `TestExecution` which is used to obtain arguments for next two calls.  

This is part of flank legacy. Those api calls provides data for `JUnitResult`. 
As `JUnitResult` contains all data required to generate `table` output, we can reuse it.
In result, we are forced to calculate `flaky` outcomes on flank site because of `step`.
Probably it is place for little improvement in the future.

### Test details calculation
When flank and gcloud implementations can be slightly different because of programming languages,
the logic behind is the mainly same.
