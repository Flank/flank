#!/usr/bin/env bash

EARL_GREY_EXAMPLE="$TEST_PROJECTS_IOS/EarlGreyExample"

function setup_ios_env() {
  if ! [ -x "$(command -v xcpretty)" ]; then
    gem install cocoapods
  fi
  (cd "$EARL_GREY_EXAMPLE" && pod install)
}

function install_xcpretty() {
  if ! [ -x "$(command -v xcpretty)" ]; then
    gem install xcpretty
  fi
}

function universal_framework() {
  "$EARL_GREY_EXAMPLE/universal_framework.sh"
}

function earl_grey_example() {
  local dir=$EARL_GREY_EXAMPLE
  local buildDir="$dir/build"

  for arg in "$@"; do case "$arg" in

    '--generate' | '-g')

      install_xcpretty

      rm -rf "$buildDir"

      xcodebuild build-for-testing \
        -allowProvisioningUpdates \
        -workspace "$dir/EarlGreyExample.xcworkspace" \
        -scheme "EarlGreyExampleSwiftTests" \
        -derivedDataPath "$buildDir" \
        -sdk iphoneos |
        xcpretty

      xcodebuild build-for-testing \
        -allowProvisioningUpdates \
        -workspace "$dir/EarlGreyExample.xcworkspace" \
        -scheme "EarlGreyExampleTests" \
        -derivedDataPath "$buildDir" \
        -sdk iphoneos |
        xcpretty
      ;;

    '--copy' | '-c')

      local productsDir="$dir/build/Build/Products"

      cp -Rf "$productsDir"/*-iphoneos "$FLANK_FIXTURES_TMP/"

      cp "$productsDir"/*.xctestrun "$FLANK_FIXTURES_TMP/"

      cp \
        "$productsDir/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleTests.xctest/EarlGreyExampleTests" \
        "$FLANK_FIXTURES_TMP/objc/"

      cp \
        "$productsDir/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests" \
        "$FLANK_FIXTURES_TMP/swift/"
      ;;

    esac done
}

echo "iOS test projects ops loaded"
