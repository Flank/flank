#!/usr/bin/env bash

set -euxo pipefail

DIR=`dirname "$BASH_SOURCE"`

TEST_APP="$DIR/.."

APK="$DIR/apk"

BUILD_OUTPUT="$TEST_APP/app/build/outputs/apk"

mkdir $APK || true

rm $APK/app-*.apk || true

"$TEST_APP/gradlew" -p "$TEST_APP" clean assemble assembleAndroidTest

cp $BUILD_OUTPUT/singleSuccess/debug/app-single-success-debug.apk $APK/app-debug.apk

cp $BUILD_OUTPUT/androidTest/**/debug/*.apk $APK/
