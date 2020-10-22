#!/usr/bin/env bash

function setup_ios_env() {
  $DIR/../../flank-scripts/bash/flankScripts shell setup_ios_env
}

function install_xcpretty() {
  $DIR/../../flank-scripts/bash/flankScripts shell install_xcpretty
}

function universal_framework() {
   $DIR/../../flank-scripts/bash/flankScripts shell iosUniversalFramework
}

function earl_grey_example() {
  $DIR/../../flank-scripts/bash/flankScripts shell ops ios --copy --generate
}

echo "iOS test projects ops loaded"
