#!/usr/bin/env bash

echo "DEPRECATED!!!"

set -euxo pipefail

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../../.." >/dev/null 2>&1 && pwd )"

TEST_APP_DIR="$PROJECT_DIR/test_projects/android"
MULTI_MODULES_DIR="$PROJECT_DIR/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/multi-modules/"

"$TEST_APP_DIR/gradlew" :multi-modules:multiapp:assemble \
  :multi-modules:testModule1:assembleAndroidTest \
  :multi-modules:testModule2:assembleAndroidTest \
  :multi-modules:testModule3:assembleAndroidTest \
  :multi-modules:testModule4:assembleAndroidTest \
  :multi-modules:testModule5:assembleAndroidTest \
  :multi-modules:testModule6:assembleAndroidTest \
  :multi-modules:testModule7:assembleAndroidTest \
  :multi-modules:testModule8:assembleAndroidTest \
  :multi-modules:testModule9:assembleAndroidTest \
  :multi-modules:testModule10:assembleAndroidTest \
  :multi-modules:testModule11:assembleAndroidTest \
  :multi-modules:testModule12:assembleAndroidTest \
  :multi-modules:testModule13:assembleAndroidTest \
  :multi-modules:testModule14:assembleAndroidTest \
  :multi-modules:testModule15:assembleAndroidTest \
  :multi-modules:testModule16:assembleAndroidTest \
  :multi-modules:testModule17:assembleAndroidTest \
  :multi-modules:testModule18:assembleAndroidTest \
  :multi-modules:testModule19:assembleAndroidTest \
  :multi-modules:testModule20:assembleAndroidTest \

mkdir -p "$MULTI_MODULES_DIR"
find multi-modules -type f -name "*.apk" -exec cp {} "$MULTI_MODULES_DIR" \;
