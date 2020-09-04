#!/usr/bin/env bash

dir="$(dirname "$BASH_SOURCE")"

. "$dir/android/ops.sh"
. "$dir/ios/EarlGreyExample/ops.sh"

function update_test_artifacts() {

  for arg in "$@"; do case "$arg" in

    android)
      base_app_apk --generate --copy
      base_test_apks --generate --copy
      ;;

    ios)
      setup_ios_env
      earl_grey_example --generate --copy
      ;;

    go)
      echo "TODO"
      ;;

    all)
      update_test_artifacts android ios go
      ;;

    esac done
}
