#!/usr/bin/env bash

function setup_ios_env() {
  if ! [ -x "$(command -v xcpretty)" ]; then
    gem install cocoapods
  fi
  local dir=$(dirname "$BASH_SOURCE")
  (cd "$dir" && pod install)
}

function install_xcpretty() {
  if ! [ -x "$(command -v xcpretty)" ]; then
    gem install xcpretty
  fi
}

function universal_framework() {
  local dir=$(dirname "$BASH_SOURCE")
  "$dir/universal_framework.sh"
}

function earl_grey_example() {
  local dir=$(dirname "$BASH_SOURCE")
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

      cp -R "$productsDir/*-iphoneos" "$FLANK_FIXTURES_TMP/"

      cp "$productsDir/*.xctestrun" "$FLANK_FIXTURES_TMP/"

      cp \
        "$productsDir/Debug-iphoneos/EarlGreyExampleTests.xctest/EarlGreyExampleTests" \
        "$FLANK_FIXTURES_TMP/objc/"

      cp \
        "$productsDir/Debug-iphoneos/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests" \
        "$FLANK_FIXTURES_TMP/swift/"
      ;;

    esac done
}

echo "iOS test projects ops loaded"
