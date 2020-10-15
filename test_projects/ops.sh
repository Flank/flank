#!/usr/bin/env bash

TEST_PROJECTS_ANDROID="$TEST_PROJECTS/android"
TEST_PROJECTS_IOS="$TEST_PROJECTS/ios"

. "$TEST_PROJECTS_ANDROID/ops.sh"
. "$TEST_PROJECTS_IOS/EarlGreyExample/ops.sh"
. "$TEST_PROJECTS_IOS/FlankExample/ops.sh"

function update_test_artifacts() {

  for arg in "$@"; do case "$arg" in

    android)
      base_app_apk --generate --copy
      base_test_apks --generate --copy
      ;;

    ios)
      setup_ios_env
      earl_grey_example --generate --copy
      flank_ios_example --generate --copy
      ;;

    go)
      cp -R "$FLANK_ROOT/test_projects/gohello" "$FLANK_FIXTURES_TMP/"
      ;;

    all)
      update_test_artifacts android ios go
      ;;

    esac done
}
