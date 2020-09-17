#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

FLANK="$DIR/.."

"$FLANK/../gradlew" test_runner:clean test_runner:assemble test_runner:shadowJar

cp "$FLANK"/build/libs/flank.jar "$DIR/flank.jar"
