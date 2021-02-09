#!/usr/bin/env bash

function setup_ios_env() {
  $DIR/../../flank-scripts/bash/flankScripts dependencies setup_ios_env
}

function install_xcpretty() {
  $DIR/../../flank-scripts/bash/flankScripts dependencies install_xcpretty
}

function universal_framework() {
   $DIR/../../flank-scripts/bash/flankScripts dependencies universal_framework_files
}

function earl_grey_example() {
  $DIR/../../flank-scripts/bash/flankScripts assemble io earl_grey --copy --generate
}

echo "iOS EarlGreyExample test projects ops loaded"
