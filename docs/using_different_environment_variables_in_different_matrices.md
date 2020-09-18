# Using different environment variables per test apk

## The problem

Environment variables are used to configure test coverage. When you configure this in the global scope (gcloud:environment-variables) all of the matrices have the same test coverage file name.
Example:

```yml

gcloud:
  app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
  test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk
  environment-variables:
    coverage: true
    coverageFile: /sdcard/coverage.ec
    clearPackageData: true
  directories-to-pull:
    - /sdcard/
  use-orchestrator: false

```

In the case where you have configured additional test apks, all of the matrices have a coverage file named ```coverage.ec```

## Solution

You can override environment variables by configuring it in ```additional-app-test-apks```

Example:

```yml

gcloud:
  app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
  test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk
  environment-variables:
    coverage: true
    coverageFile: /sdcard/main.ec
    clearPackageData: true
  directories-to-pull:
    - /sdcard/
  use-orchestrator: false

flank:
  disable-sharding: false
  max-test-shards: 2
  files-to-download:
    - .*/sdcard/[^/]+\.ec$
  additional-app-test-apks:
    - test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk
      environmentVariables:
        coverageFile: /sdcard/module_1.ec
    - test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk
      environmentVariables:
        coverageFile: /sdcard/module_2.ec

```

Now Flank override ```coverageFile``` in matrices and you can identify what matrix run what test
