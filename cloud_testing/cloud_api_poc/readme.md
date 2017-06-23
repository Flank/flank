# Cloud API Proof of Concept

A small proof of concept project that demos using:

- `google-cloud-storage`
- `google-api-services-toolresults`
- `google-api-services-testing`

## Design

- APKs are uploaded to Google Cloud Storage (`google-cloud-storage`)
- DexParser enumerates test methods from the test APK
- Tests are scheduled to run on Firebase Test Lab in parallel (`google-api-services-testing`)
- Matrix ids are polled sequentially until all tests have finished
- Results are fetched in parallel (`google-api-services-toolresults`)
- Test counts, billing info, and test metadata are printed
- Exit code is set based on overall test results

## Environment Variables

Note that if you have gcloud CLI setup then everything should be auto detected without setting
any environment variables.

- `GOOGLE_APPLICATION_CREDENTIALS` - Path to json secrets. `~/.config/gcloud/application_default_credentials.json`
- `GOOGLE_CLOUD_PROJECT` - Gcloud project will be read from credentials.json unless this env var is set.

## To Do

- Add bucket/object creation logic
- Config file support
- Other types of sharding

## Output

```
Test runner started.
Found 1 fully qualified test methods
Finished in 115 ms
Running 1 tests
0 / 1 complete
VALIDATING
PENDING
PENDING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
RUNNING
FINISHED
Test run finished
1 / 1 complete.

Fetching test results...
Billable minutes: 1m
Test duration: 6s
Run duration: 264s
Name: Instrumentation test
Targets: class com.example.app.ExampleInstrumentedTest#useAppContext
Dimensions:
  Model: NexusLowRes
  Version: 25
  Locale: en
  Orientation: portrait

Outcome: success
https://console.firebase.google.com/project/delta-essence-114723/testlab/histories/bh.58317d9cd7ab9ba2/matrices/6833572347406061883

1 tests. successful: 1, failed: 0
Billable minutes: 1
Physical device cost: $0.08
Virtual  device cost: $0.02
Finished in 4m 33s

Process finished with exit code 0
```
