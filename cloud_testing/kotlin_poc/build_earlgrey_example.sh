#!/usr/bin/env bash

# Requires:
#  - Xcode 9.3
#  - Ruby 2.5
#  - Cocoapods 1.5.2
#  - EarlGrey gem 1.13.0
#
# See getting started guide:
#  - https://github.com/google/EarlGrey/tree/master/Demo/EarlGreyExample

REPO_NAME="EarlGrey"
if [ ! -d "$REPO_NAME" ]; then
  git clone https://github.com/google/$REPO_NAME.git
fi

DIR=$(pwd)

cd "$DIR/$REPO_NAME/Demo/EarlGreyExample"

pod install

DD_PATH="$DIR/xctestrun/"
rm -rf "$DD_PATH"


echo "open $DIR/$REPO_NAME/Demo/EarlGreyExample/EarlGreyExample.xcworkspace"
echo "Manually update with a valid Apple id."
echo "[Press Enter to continue]"
read

xcodebuild build-for-testing \
  -workspace EarlGreyExample.xcworkspace \
  -scheme "EarlGreyExampleSwiftTests" \
  -destination "generic/platform=iOS" \
  -derivedDataPath "$DD_PATH"

FIXTURES_PATH="$DIR/src/main/kotlin/xctest/fixtures/swift"
cp "$DIR/xctestrun/Build/Products/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests" \
 "$FIXTURES_PATH"
