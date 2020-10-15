#!/usr/bin/env bash

FLANK_IOS_EXAMPLE="$TEST_PROJECTS_IOS/FlankExample"

function flank_ios_example() {
  local dir=$FLANK_IOS_EXAMPLE
  local buildDir="$dir/build"

  for arg in "$@"; do case "$arg" in

    '--generate' | '-g')

      rm -rf "$buildDir"

      xcodebuild build-for-testing \
      -allowProvisioningUpdates \
      -project "$dir/FlankExample.xcodeproj" \
      -scheme "FlankTests" \
      -derivedDataPath "$buildDir" \
      -sdk iphoneos |
      xcpretty
      ;;

    '--copy' | '-c')

      local productsDir="$dir/build/Build/Products"

      # xcodebuild generates .xctestrun files names in format: PROJECTNAME_platform_version_architecture.xctestrun, code below removes "_platform_version_architecture" part
      mv -f "$productsDir/"*.xctestrun "$productsDir/FlankExampleTests.xctestrun"

      mkdir -p "$FLANK_FIXTURES_TMP/ios/flank_ios_example/"
      
      cp -Rf "$productsDir"/*-iphoneos "$FLANK_FIXTURES_TMP/ios/flank_ios_example/"

      cp "$productsDir"/*.xctestrun "$FLANK_FIXTURES_TMP/ios/flank_ios_example/"
      ;;

    esac done
}

echo "iOS Flank Example test projects ops loaded"
