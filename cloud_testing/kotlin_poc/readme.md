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

There are two possible ways of authenticating against google.

### Using your google account

- `gcloud components update`
- `gcloud config set project <PROJECT-ID>`
- `gcloud auth login`
    - This logs you in for `gcloud` and `gsutil`
- `gcloud auth application-default login`
    - This logs you in for the gcloud sdks (and therefore Flank as well). It will save the application default
      credential to `~/.config/gcloud/application_default_credentials.json`
- Verify gcloud can [run Android](https://firebase.google.com/docs/test-lab/android/continuous) and [iOS tests.](https://firebase.google.com/docs/test-lab/ios/command-line)

### Using service account

Follow the [google docs](https://firebase.google.com/docs/test-lab/android/continuous) to create a service account
- `gcloud components update`
- Set `GOOGLE_APPLICATION_CREDENTIALS` env variable with a custom path for `credentials.json`
- Set `GOOGLE_CLOUD_PROJECT` env var with your project id (alternatively set via flank.yml)

