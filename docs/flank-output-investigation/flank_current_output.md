# Flank output

## Android

### Version section

```bash

version: local_snapshot
revision: d5e7c76ab206373c6cdf0c1a25e2575f323e5929
session id: 7a7ceda1-658e-461f-bd37-04f2b2a90a37

```

### Args section

```bash

AndroidArgs
    gcloud:
      results-bucket: test-lab-v9cn46bb990nx-kz69ymd4nm9aq
      results-dir: 2021-03-01_13-01-56.722225_lYrQ
      record-video: false
      timeout: 15m
      async: false
      client-details:
      network-profile: null
      results-history-name: null
      # Android gcloud
      app: /Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
      test: /Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
      additional-apks:
      auto-google-login: false
      use-orchestrator: true
      directories-to-pull:
      grant-permissions: all
      type: null
      other-files:
      scenario-numbers:
      scenario-labels:
      obb-files:
      obb-names:
      performance-metrics: false
      num-uniform-shards: null
      test-runner-class: null
      test-targets:
      robo-directives:
      robo-script: null
      device:
        - model: NexusLowRes
          version: 28
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0
      test-targets-for-shard:
      fail-fast: false

    flank:
      max-test-shards: 1
      shard-time: -1
      num-test-runs: 1
      smart-flank-gcs-path: 
      smart-flank-disable-upload: false
      default-test-time: 120.0
      use-average-test-time-for-new-tests: false
      files-to-download:
      test-targets-always-run:
      disable-sharding: true
      project: flank-open-source
      local-result-dir: results
      full-junit-result: false
      # Android Flank Yml
      keep-file-path: false
      additional-app-test-apks:
      run-timeout: -1
      legacy-junit-result: false
      ignore-failed-tests: false
      output-style: single
      disable-results-upload: false
      default-class-test-time: 240.0
      disable-usage-statistics: false
      output-report: none

```

### Run tests section

```bash

RunTests
  Saved 1 shards to /Users/adamfilipowicz/Repos/flank/results/2021-03-01_13-01-56.722225_lYrQ/android_shards.json
  Uploading [android_shards.json] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...
  Uploading [app-debug.apk] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...
  Uploading [app-single-success-debug-androidTest.apk] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...

  1 test / 1 shard

  Uploading [session_id.txt] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...
  1 matrix ids created in 0m 5s
  Raw results will be stored in your GCS bucket at [https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ]


```

### Matrices webLink section

```bash

  matrix-1kozhsv2imkru https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/8233077922466140188/executions/bs.d3f60304f671ce86

```

### Test status

```bash

  3m 11s Test executions status: FINISHED:1
  3m 11s matrix-1kozhsv2imkru FINISHED

```

### Cost report section

```bash

CostReport
  Virtual devices
    $0.02 for 1m

  Uploading [CostReport.txt] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...

```

### Results section

```bash

MatrixResultsReport
  1 / 1 (100.00%)
┌─────────┬──────────────────────┬────────────────────────────┬────────────────────────────────┐
│ OUTCOME │      MATRIX ID       │      TEST AXIS VALUE       │          TEST DETAILS          │
├─────────┼──────────────────────┼────────────────────────────┼────────────────────────────────┤
│ success │ matrix-1kozhsv2imkru │ NexusLowRes-28-en-portrait │ 1 test cases passed, 1 skipped │
└─────────┴──────────────────────┴────────────────────────────┴────────────────────────────────┘
  Uploading [MatrixResultsReport.txt] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...
  Uploading [JUnitReport.xml] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...
  Uploading [matrix_ids.json] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-03-01_13-01-56.722225_lYrQ/...

FetchArtifacts
    Updating matrix file

Matrices webLink
  matrix-1kozhsv2imkru https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/8233077922466140188/executions/bs.d3f60304f671ce86

```

## Ios

### Version section

```bash

version: local_snapshot
revision: d5e7c76ab206373c6cdf0c1a25e2575f323e5929
session id: 7a7ceda1-658e-461f-bd37-04f2b2a90a37

```

### Args section

```bash

IosArgs
    gcloud:
      results-bucket: test-lab-v9cn46bb990nx-kz69ymd4nm9aq
      results-dir: test_dir
      record-video: false
      timeout: 15m
      async: false
      client-details:
      network-profile: null
      results-history-name: null
      # iOS gcloud
      test: /Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
      xctestrun-file: /Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
      xcode-version: null
      device:
        - model: iphone8
          version: 13.6
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0
      directories-to-pull:
      other-files:
      additional-ipas:
      scenario-numbers:
      type: xctest
      app: 
      test-special-entitlements: false
      fail-fast: false

    flank:
      max-test-shards: 1
      shard-time: -1
      num-test-runs: 1
      smart-flank-gcs-path: 
      smart-flank-disable-upload: false
      default-test-time: 120.0
      use-average-test-time-for-new-tests: false
      test-targets-always-run:
      files-to-download:
      keep-file-path: false
      full-junit-result: false
      # iOS flank
      test-targets:
      disable-sharding: false
      project: flank-open-source
      local-result-dir: results
      run-timeout: -1
      ignore-failed-tests: false
      output-style: single
      disable-results-upload: false
      default-class-test-time: 240.0
      disable-usage-statistics: false
      only-test-configuration: 
      skip-test-configuration: 
      output-report: none
WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled

```

### Run tests section

```bash

RunTests
Found xctest: /Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest
isMacOS = true (mac os x)
nm -U "/Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests"
nm -gU "/Users/adamfilipowicz/Repos/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests" | xargs -s 262144 xcrun swift-demangle

 Smart Flank cache hit: 0% (0 / 17)
  Shard times: 2040s

  Saved 1 shards to /Users/adamfilipowicz/Repos/flank/results/test_dir/ios_shards.json
  Uploading [ios_shards.json] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/...

  17 tests / 1 shard

  Uploading [EarlGreyExample.zip] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/.....
  Uploading [EarlGreyExampleSwiftTests_shard_0.xctestrun] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/shard_0/...
  Uploading [session_id.txt] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/...
  1 matrix ids created in 0m 29s
  Raw results will be stored in your GCS bucket at [https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir]

```

### Matrices webLink section

```bash

Matrices webLink
  matrix-3d331uzs97mlt https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.a3b607c9bb6d0088/matrices/5523514684002242128/executions/bs.70a259d113387c0c

```

### Test status

```bash

  3m 11s Test executions status: FINISHED:1
  3m 11s matrix-1kozhsv2imkru FINISHED

```

### Cost report section

```bash

CostReport
  Physical devices
    $0.08 for 1m

  Uploading [CostReport.txt] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/...


```

### Results section

```bash

MatrixResultsReport
  1 / 1 (100.00%)
┌─────────┬──────────────────────┬──────────────────────────┬──────────────────────┐
│ OUTCOME │      MATRIX ID       │     TEST AXIS VALUE      │     TEST DETAILS     │
├─────────┼──────────────────────┼──────────────────────────┼──────────────────────┤
│ success │ matrix-3d331uzs97mlt │ iphone8-13.6-en-portrait │ 17 test cases passed │
└─────────┴──────────────────────┴──────────────────────────┴──────────────────────┘
  Uploading [MatrixResultsReport.txt] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/...
  Uploading [JUnitReport.xml] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/...
  Uploading [matrix_ids.json] to https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/test_dir/...

FetchArtifacts
    Updating matrix file

Matrices webLink
  matrix-3d331uzs97mlt https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.a3b607c9bb6d0088/matrices/5523514684002242128/executions/bs.70a259d113387c0c

```
