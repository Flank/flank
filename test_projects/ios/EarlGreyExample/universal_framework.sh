#!/bin/bash

set -euxo pipefail

COMBO="./ios-frameworks"
DEVICE="$COMBO/Debug-iphoneos"
SIM="$COMBO/Debug-iphonesimulator"

lipo -create $DEVICE/libChannelLib.a $SIM/libChannelLib.a -output $COMBO/libChannelLib.a
lipo -create $DEVICE/libCommonLib.a $SIM/libCommonLib.a -output $COMBO/libCommonLib.a
lipo -create $DEVICE/libeDistantObject.a $SIM/libeDistantObject.a -output $COMBO/libeDistantObject.a
lipo -create $DEVICE/libTestLib.a $SIM/libTestLib.a -output $COMBO/libTestLib.a
lipo -create $DEVICE/libUILib.a $SIM/libUILib.a -output $COMBO/libUILib.a

cp -RL $DEVICE/AppFramework.framework $COMBO/AppFramework.framework
DEVICE_FRAMEWORK="$DEVICE/AppFramework.framework/AppFramework"
SIM_FRAMEWORK="$SIM/AppFramework.framework/AppFramework"
UNI_FRAMEWORK="$COMBO/AppFramework.framework/AppFramework"

lipo -create \
  "$DEVICE_FRAMEWORK" \
  "$SIM_FRAMEWORK" \
  -output "$UNI_FRAMEWORK"

dsymutil "$UNI_FRAMEWORK" \
  --out "$COMBO/AppFramework.framework.dSYM"
