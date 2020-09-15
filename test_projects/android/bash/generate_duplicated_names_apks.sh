#!/usr/bin/env bash

echo "DEPRECATED!!! use functions from test_projects/android/ops.sh"

set -euxo pipefail

DIR=`dirname "$BASH_SOURCE"`
TEST_APP_DIR="$DIR/.."

"$TEST_APP_DIR/gradlew" -p "$TEST_APP_DIR" \
  app:assemble \
  dir0:testModule:assembleAndroidTest \
  dir1:testModule:assembleAndroidTest \
  dir2:testModule:assembleAndroidTest \
  dir3:testModule:assembleAndroidTest \

APKS_DIR="$TEST_APP_DIR/../../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/duplicated_names"
for INDEX in 0 1 2 3
do
  DIR_NAME="dir$INDEX"
  APK_DIR="$APKS_DIR/$DIR_NAME/"
  mkdir -p "$APK_DIR"
  cp $TEST_APP_DIR/$DIR_NAME/testModule/build/outputs/apk/**/debug/*.apk "$APK_DIR"
done

cp "$TEST_APP_DIR/app/build/outputs/apk/singleSuccess/debug/app-single-success-debug.apk" "$APKS_DIR/app-debug.apk"

