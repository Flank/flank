#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

function update_test_artifacts() {

  for arg in "$@"; do case "$arg" in

    android)
      $DIR/../flank-scripts/bash/flankScripts shell ops android --copy --generate
      ;;

    ios)
      kotlin $DIR/../flank-scripts/bash/flankScripts shell ops ios --copy --generate
      ;;

    go)
      kotlin $DIR/../flank-scripts/bash/flankScripts shell ops go --copy --generate
      ;;

    all)
      kotlin $DIR/../flank-scripts/bash/flankScripts shell ops android --copy --generate
      kotlin $DIR/../flank-scripts/bash/flankScripts shell ops ios --copy --generate
      kotlin $DIR/../flank-scripts/bash/flankScripts shell ops go --copy --generate
      ;;

    esac done
}
