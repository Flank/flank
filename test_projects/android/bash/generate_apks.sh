#!/usr/bin/env bash

echo "DEPRECATED!!! use functions from test_projects/android/ops.sh"

set -euxo pipefail

APK_OUTPUT=$1

DIR=`dirname "$BASH_SOURCE"`

TEST_APP="$DIR/.."

BUILD_OUTPUT="$TEST_APP/app/build/outputs/apk"

mkdir $APK_OUTPUT || true

"$TEST_APP/gradlew" -p "$TEST_APP" clean assemble assembleAndroidTest

cp $BUILD_OUTPUT/singleSuccess/debug/app-single-success-debug.apk $APK_OUTPUT/app-debug.apk

cp $BUILD_OUTPUT/androidTest/**/debug/*.apk $APK_OUTPUT/
