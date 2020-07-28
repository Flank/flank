#!/bin/bash

set -euxo pipefail

if ! [ -x "$(command -v xcpretty)" ]; then
  gem install xcpretty
fi

DD="dd_tmp"
SCHEME="EarlGreyExampleSwiftTests"
ZIP="ios_earlgrey2.zip"

rm -rf "$DD"

xcodebuild build-for-testing \
  -allowProvisioningUpdates \
  -workspace ./EarlGreyExample.xcworkspace \
  -scheme "$SCHEME" \
  -derivedDataPath "$DD" \
  -sdk iphoneos \
  | xcpretty

pushd "$DD/Build/Products"
zip -r "$ZIP" *-iphoneos *.xctestrun
popd
mv "$DD/Build/Products/$ZIP" .
