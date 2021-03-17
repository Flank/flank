# flutter_example

Flutter Flank Example

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://flutter.dev/docs/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://flutter.dev/docs/cookbook)

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## Run integration tests on local android device / emulator

```bash

flutter drive \
  --driver=test_driver/integration_test.dart \
  --target=integration_tests/success_test.dart

```

## Build apk with tests

```bash

./build_flutter_example

```

results should be stored in ```./build/app/outputs/apk```

## Run integration tests on gcloud

simple run 

```bash

./build_and_run_tests_firebase

```

