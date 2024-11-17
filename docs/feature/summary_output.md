# Formatted summary output 
```
┌─────────┬──────────────────────┬──────────────────────────┬─────────────────────────────────────────┐
│ OUTCOME │      MATRIX ID       │     TEST AXIS VALUE      │              TEST DETAILS               │
├─────────┼──────────────────────┼──────────────────────────┼─────────────────────────────────────────┤
│ success │ matrix-35czp85w4h3a7 │ greatqlte-26-en-portrait │ 20 test cases passed                    │
│ failure │ matrix-35czp85w4h3a7 │ Nexus6P-26-en-portrait   │ 1 test cases failed, 16 passed, 3 flaky │
└─────────┴──────────────────────┴──────────────────────────┴─────────────────────────────────────────┘
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

### Outcome calculation v2 [#919](https://github.com/Flank/flank/pull/919)
Because of [rate limit issue](../bugs/891-rate-limit-exceeded.md), the new implementation of test details calculation is 
based on gcloud's [CreateMatrixOutcomeSummaryUsingEnvironments](https://github.com/Flank/gcloud_cli/blob/3c30bb59d18fa68c5a6df7d115786bc23f5fc224/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/results_summary.py#L161).
which was already described in `Outcome calculation v1` section.

* The activity diagram for [gcloud](../gcloud/firebase/test/results_summary.puml)

![results_summary.puml](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/gcloud/firebase/test/results_summary.puml)

* The activity diagram for [flank](../gcloud/firebase/test/results_summary.puml)

![outcome_details_billable_minutes_activity.puml](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1fdd4b78a7c6db5cc31165d75816e6957aea86c1/docs/feature/outcome_details_billable_minutes_activity.puml)

Both diagrams are showing slightly different aspects of flow but the main difference between `gcloud` and `flank` is that the `flank` is calculating `billable minutes` in addition.
The `billable` minutes are able to calculate from list of `steps`, 
so while `gcloud` is fetching `steps` only when `environments` are corrupted, the flank always required at least `steps`.

#### Drawbacks
* Calculating outcome details basing on `steps` may not return info about flaky tests.
But it is possible to reuse data that is collecting for `JUnitReport` as alternative way for generating outcome details. It should be delivered in dedicated pull request.
* Flank is not displaying info about the environment in outcome table this problem is described in [#983](https://github.com/Flank/flank/issues/983) issue.

### Outcome calculation v1
It should be mentioned there are some crucial differences how flank and gcloud calculates outcome value.

Gcloud is using following API calls
1. `self._client.projects_histories_executions_environments.List(request)`
2. `self._client.projects_histories_executions_steps.List(request)`

The first one is default, but if returns any `environment` without `environmentResult.outcome`, the second one will be used to obtain `steps`. 
Both `environments` and `steps` can provide `outcome`. The difference between them is the `steps` returns `success` event if tests are `flaky`.
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
