#!/bin/bash

set -euo pipefail

./gradlew clean build

APK_ROOT=../test_app/apks
APP_APK=$APK_ROOT/app-debug.apk
TEST_APK=$APK_ROOT/app-debug-androidTest.apk

set -x
java -jar ./build/libs/Flank-*.jar -a $APP_APK -t $TEST_APK -f "class com.example.app.ExampleUiTest#testHelloWorld"
