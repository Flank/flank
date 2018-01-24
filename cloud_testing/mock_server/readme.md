# test runner proof of concept

Implementation details:

- API based
  - No shelling out to a python process
- Kotlin coroutines
  - Replaces threadpool

## Notes

- Disabling video recording causes infrastructure failures due to AVD idle timeout
- Enabling orchestrator breaks pulling screenshots from `/sdcard/screenshots`

## workflow

`Main.kt` - runs the tests
`reports/CostSummary` - generates a billing report (`cost_summary.txt`) from `matrix_ids.json`
`reports/MergeJUnitResults` - generates a test flakiness report (`summary.csv`) from `matrix_ids.json`

## todo

- Android Studio import test results
