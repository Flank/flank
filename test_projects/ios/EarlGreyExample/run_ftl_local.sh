#!/bin/bash

set -euxo pipefail

DD="dd_tmp"
SCHEME="appUITests"
ZIP="ios_earlgrey2.zip"

# Firebase test lab runs using -xctestrun
xcodebuild test-without-building \
  -xctestrun $DD/Build/Products/*.xctestrun \
  -derivedDataPath "$DD" \
  -destination 'id=ADD_YOUR_ID_HERE'

# get device identifier in Xcode -> Window -> Devices and Simulators
