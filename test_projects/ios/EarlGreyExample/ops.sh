#!/usr/bin/env bash

function setup_ios_env() {
  local dir=$(dirname "$BASH_SOURCE")
  gem install cocoapods
  (cd "$dir" && pod install)
}

function earl_grey_example() {
  local dir=$(dirname "$BASH_SOURCE")
  for arg in "$@"; do
    case "$arg" in

    '--generate' | '-g')
      "$dir/build_example.sh"
      ;;

    '--copy' | '-c')
      echo "TODO"
      ;;

    esac
  done
}

function earl_grey_ftl() {
  local dir=$(dirname "$BASH_SOURCE")
  for arg in "$@"; do
    case "$arg" in

    '--generate' | '-g')
      "$dir/build_ftl.sh"
      ;;

    '--copy' | '-c')
      echo "--copy TODO"
      ;;

    esac
  done
}

function universal_framework() {
  local dir=$(dirname "$BASH_SOURCE")
  "$dir/universal_framework.sh"
}

echo "iOS test projects ops loaded"
