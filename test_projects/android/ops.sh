#!/usr/bin/env bash

function base_app_apk() {
  local dir=$TEST_PROJECTS_ANDROID
  local outputDir="$FLANK_FIXTURES_TMP/apk"

  for arg in "$@"; do case "$arg" in

    '--generate' | '-g')
      "$dir/gradlew" -p "$dir" app:assemble
      ;;

    '--copy' | '-c')
      local apkIn="$dir/app/build/outputs/apk/singleSuccess/debug/app-single-success-debug.apk"
      local apkOut="$outputDir/app-debug.apk"

      mkdir -p "$outputDir"
      cp "$apkIn" "$apkOut"
      ;;

    esac done
}

# depends on base_app_apk
function base_test_apks() {
  local dir=$TEST_PROJECTS_ANDROID

  for arg in "$@"; do case "$arg" in

    '--generate' | '-g')
      "$dir/gradlew" -p "$dir" app:assembleAndroidTest
      ;;

    '--copy' | '-c')
      local outputDir="$FLANK_FIXTURES_TMP/apk"
      local testIn="$dir/app/build/outputs/apk/androidTest/**/debug/*.apk"

      mkdir -p "$outputDir"
      cp $testIn $outputDir/
      ;;

    esac done
}

# depends on base_app_apk
function duplicated_names_apks() {
  local dir=$TEST_PROJECTS_ANDROID

  for arg in "$@"; do case "$arg" in

    '--generate' | '-g')
      "$dir/gradlew" -p "$dir" \
        dir0:testModule:assembleAndroidTest \
        dir1:testModule:assembleAndroidTest \
        dir2:testModule:assembleAndroidTest \
        dir3:testModule:assembleAndroidTest
      ;;

    '--copy' | '-c')
      local outputDir="$FLANK_FIXTURES_TMP/apk"
      local testIn="$dir/app/build/outputs/apk/androidTest/**/debug/*.apk"

      mkdir -p "$outputDir"
      local dir=$(dirname "${BASH_SOURCE[0]-$0}")
      local outputDir="$FLANK_FIXTURES_TMP/apk/duplicated_names/"

      for index in 0 1 2 3; do
        moduleName="dir$index"
        apkDir="$outputDir/$moduleName/"
        mkdir -p "$apkDir"
        cp "$dir/$moduleName"/testModule/build/outputs/apk/**/debug/*.apk "$apkDir"
      done
      ;;

    esac done
}

function multi_module_apks() {
  local dir=$TEST_PROJECTS_ANDROID
  local outputDir="$FLANK_FIXTURES_TMP/apk/multi-modules/"

  for arg in "$@"; do case "$arg" in

    '--generate' | '-g')
      "$dir/gradlew" -p "$dir" \
        :multi-modules:multiapp:assemble \
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
        :multi-modules:testModule20:assembleAndroidTest
      ;;

    '--copy' | '-c')
      mkdir -p "$outputDir"
      find "$dir/multi-modules" -type f -name "*.apk" -exec cp {} "$outputDir" \;
      ;;

    esac done
}

echo "Android test projects ops loaded"
