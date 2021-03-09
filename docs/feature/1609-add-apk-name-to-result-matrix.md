# Add apk name or module name to the result matrix

Currently, the result matrix displays matrices ids only. When there are lots of additional test apks it's hard to distinguish which apk has failed. Having the apk file name associated with the results (matrices) will give a better overview and will allow a user to find a failing apk module without the need of jumping between result URLs.

Current result table:
```
┌─────────┬──────────────────────┬────────────────────────────┬───────────────────────────────────────────┐
│ OUTCOME │      MATRIX ID       │      TEST AXIS VALUE       │               TEST DETAILS                │
├─────────┼──────────────────────┼────────────────────────────┼───────────────────────────────────────────┤
│ failure │ matrix-b1fizzmw53qka │ NexusLowRes-29-de-portrait │ 82 test cases failed, 206 passed, 1 flaky │
│ failure │ matrix-wq8upcrn5uoca │ NexusLowRes-29-de-portrait │ 5 test cases failed, 4 passed             │
│ failure │ matrix-2n50loc2ze1h4 │ NexusLowRes-29-de-portrait │ 1 test cases failed, 6 passed             │
│ success │ matrix-3eo91na6dzmd1 │ NexusLowRes-29-de-portrait │ 8 test cases passed                       │
│ failure │ matrix-1ztkep4prcq43 │ NexusLowRes-29-de-portrait │ 1 test cases failed                       │
│ failure │ matrix-w9wpp0jwt4yba │ NexusLowRes-29-de-portrait │ 23 test cases failed, 6 passed            │
│ success │ matrix-30cqwb4sc1rqh │ NexusLowRes-29-de-portrait │ 17 test cases passed                      │
│ success │ matrix-32kzu6e7a1t3h │ NexusLowRes-29-de-portrait │ 2 test cases passed                       │
│ failure │ matrix-3dp8qqblpyav7 │ NexusLowRes-29-de-portrait │ 20 test cases failed, 12 passed           │
│ failure │ matrix-zw52wsouk4zma │ NexusLowRes-29-de-portrait │ 1 test cases failed                       │
│ INVALID │ matrix-36qxkc2szybc6 │                            │ NO_TEST_RUNNER_CLASS                      │
└─────────┴──────────────────────┴────────────────────────────┴───────────────────────────────────────────┘
```

# References

- [Issue #1609](https://github.com/Flank/flank/issues/1609)

# Motivation

Flank gives the possibility to run multiple apks in parallel within a single test run. This feature is extensively used by companies with big projects that include a multitude of additional modules. Unfortunately, processing results in the current form is neither easy nor straightforward. The user needs to associate the matrix with the related apk/modules which can be time-consuming and error-prone. To achieve a better result overview, flank should add the apk name to both the result table and the output JSON.

# Goals

Add an associated apk file name to the result table and output JSON.

# Design

`Flank` is not aware of any project, modules, etc so it can't find module names on its own, in a reasonable way, without breaking backward compatibility.

On the other hand, finding the apk file name is simple and straightforward. To achieve it we need:
1. add new field `app` to the `SavedMatrix` data class
2. update private extension function `SavedMatrix#updateProperties` to extract apk file name from the `newMatrix`
   * find app used during tests under the `TestMatrix` -> `TestSpecification`
   * once we have `TestSpecification`, iterate over all specs (android/ios/instrumentation/etc.) and find non-null
   * since we can run only one spec within a matrix, only one (spec) is non-null
   * fetch `appApk` from spec (this is full `gs://` path) `TestSpecification#appApk`
   * extract file name from `gs://` path
   * logic should be converted to a separate function to preserve readability
   * Google API likes to return `null` - flank should handle it!
   * the same for empty file name (who knows what Google API will bring)
3. Update `SavedMatrixTableUtil` and add new column
4. Update `OutputReportLoggers` to log apk file name:
   * switch `it.matrixId` to `it.app` in:
   ```kotlin
    matrices.map {
      it.matrixId to it.testAxises // it.app
    }.toMap()
   ```
   * above is not resilient to the same name apks
   * instead, we can add a filed under matrix id in JSON
   ```json
   "matrix-1ealdqtvrtn5r": {
     "app": "my-super-app.apk",
     "test-axises": [ "results here" ]
   }
   ```

# API

No changes.

# Results

A new table column `APP` will be added to the result table. Similar for output JSON.
```
┌─────────┬──────────────────────┬──────────────────┬────────────────────────────┬───────────────────────────────────────────┐
│ OUTCOME │      MATRIX ID       │        APP       |     TEST AXIS VALUE        │               TEST DETAILS                │
├─────────┼──────────────────────┼──────────────────┼────────────────────────────┼───────────────────────────────────────────┤
│ failure │ matrix-b1fizzmw53qka │ my-super-app.apk | NexusLowRes-29-de-portrait │ 82 test cases failed, 206 passed, 1 flaky │
└─────────┴──────────────────────┴──────────────────┴────────────────────────────┴───────────────────────────────────────────┘
```

# Dependencies

There are no dependencies that will have an impact on this task.

# Testing

1. Unit test(s) should be added to verify fetching apk filename logic and its association with result matrix
1. Integration tests should be updated to verify `App` column

# Alternatives Considered

It would be possible to display a module name instead of an apk file name, it requires either:
1. additional option in yml for a user to provide module:
   * new option in `IArgs`
   * only manually
2. use [Fladle](https://github.com/runningcode/fladle) to pass module information to `flank`
   * can be automatic
   * feature available for users who use `fladle` (not available for 'flank only users)
   * requires PR for both, `fladle` and `flank`
   * strong coupling between tools
