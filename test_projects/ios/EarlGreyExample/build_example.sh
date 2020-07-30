#!/bin/bash

set -euxo pipefail

if ! [ -x "$(command -v xcpretty)" ]; then
  gem install xcpretty
fi

DD="dd_tmp"
ZIP="earlgrey_example.zip"

rm -rf "$DD"

xcodebuild build-for-testing \
  -allowProvisioningUpdates \
  -workspace ./EarlGreyExample.xcworkspace \
  -scheme "EarlGreyExampleSwiftTests" \
  -derivedDataPath "$DD" \
  -sdk iphoneos \
  | xcpretty

xcodebuild build-for-testing \
  -allowProvisioningUpdates \
  -workspace ./EarlGreyExample.xcworkspace \
  -scheme "EarlGreyExampleTests" \
  -derivedDataPath "$DD" \
  -sdk iphoneos \
  | xcpretty

pushd "$DD/Build/Products"
zip -r "$ZIP" *-iphoneos *.xctestrun
popd
mv "$DD/Build/Products/$ZIP" .
