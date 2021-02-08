#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

function update_test_artifacts() {

  for arg in "$@"; do case "$arg" in

    android)
      $DIR/../flank-scripts/bash/flankScripts assemble android app --copy --generate
      ;;

    ios)
      $DIR/../flank-scripts/bash/flankScripts shell ops ios --copy --generate
      ;;

    go)
      $DIR/../flank-scripts/bash/flankScripts assemble go --copy --generate
      ;;

    all)
      $DIR/../flank-scripts/bash/flankScripts assemble android app --copy --generate
      $DIR/../flank-scripts/bash/flankScripts shell ops ios --copy --generate
      $DIR/../flank-scripts/bash/flankScripts assemble go --copy --generate
      ;;

    esac done
}
