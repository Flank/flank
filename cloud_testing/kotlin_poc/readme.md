# test runner proof of concept

Implementation details:

- API based
  - No shelling out to a python process
- Kotlin coroutines
  - Replaces threadpool

## Notes

- Disabling video recording causes infrastructure failures due to AVD idle timeout

## workflow

`Main.kt` - runs the tests
`reports/CostSummary` - generates a billing report (`cost_summary.txt`) from `matrix_ids.json`
`reports/MergeJUnitResults` - generates a test flakiness report (`summary.csv`) from `matrix_ids.json`

## todo

- Android Studio import test results

## Authorization

- `gcloud components update`
- `gcloud config set project <PROJECT-ID>`
- `gcloud auth login`
    - This logs you in for `gcloud` and `gsutil`
- `gcloud auth application-default login`
    - This logs you in for the gcloud sdks (and therefore Flank as well). It will save the application default
      credential to `~/.config/gcloud/application_default_credentials.json`
- `GOOGLE_APPLICATION_CREDENTIALS` - optionally use a custom path for credentials.json
- `GOOGLE_CLOUD_PROJECT` - defines the project id (alternatively set via flank.yml)
- Verify gcloud can [run Android](https://firebase.google.com/docs/test-lab/android/continuous) and [iOS tests.](https://firebase.google.com/docs/test-lab/ios/command-line)
