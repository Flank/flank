#!/usr/bin/env bash

EARL_GREY_EXAMPLE="$TEST_PROJECTS_IOS/EarlGreyExample"

function setup_ios_env() {
  if ! [ -x "$(command -v xcpretty)" ]; then
    gem install cocoapods -v 1.9.3
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

      # xcodebuild generates .xctestrun files names in format: PROJECTNAME_platform_version_architecture.xctestrun, code below removes "_platform_version_architecture" part
      mv -f "$productsDir/EarlGreyExampleSwiftTests"*.xctestrun "$productsDir/EarlGreyExampleSwiftTests.xctestrun"
      mv -f "$productsDir/EarlGreyExampleTests"*.xctestrun "$productsDir/EarlGreyExampleTests.xctestrun"

      mkdir -p "$FLANK_FIXTURES_TMP/ios/earl_grey_example/objc/"
      mkdir -p "$FLANK_FIXTURES_TMP/ios/earl_grey_example/swift/"

      cp -Rf "$productsDir"/*-iphoneos "$FLANK_FIXTURES_TMP/ios/earl_grey_example/"

      cp "$productsDir"/*.xctestrun "$FLANK_FIXTURES_TMP/ios/earl_grey_example/"

      cp \
        "$productsDir/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleTests.xctest/EarlGreyExampleTests" \
        "$FLANK_FIXTURES_TMP/ios/earl_grey_example/objc/"

      cp \
        "$productsDir/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests" \
        "$FLANK_FIXTURES_TMP/ios/earl_grey_example/swift/"
      ;;

    esac done
}

echo "iOS EarlGreyExample test projects ops loaded"
