#!/usr/bin/env bash
DIR=`dirname "$BASH_SOURCE"`

function base_app_apk() {
  $DIR/../../flank-scripts/bash/flankScripts shell ops android --copy --generate --artifacts=buildBaseApk
}

# depends on base_app_apk
function base_test_apks() {
   $DIR/../../flank-scripts/bash/flankScripts shell ops android --copy --generate --artifacts=buildBaseTestApk
}

# depends on base_app_apk
function duplicated_names_apks() {
  $DIR/../../flank-scripts/bash/flankScripts shell ops android --copy --generate --artifacts=buildDuplicatedNamesApks
}

function multi_module_apks() {
  $DIR/../../flank-scripts/bash/flankScripts shell ops android --copy --generate --artifacts=buildMultiModulesApks
}

function cucumber_sample_app() {
  $DIR/../../flank-scripts/bash/flankScripts shell ops android --copy --generate --artifacts=buildCucumberSampleApp
}

echo "Android test projects ops loaded"
