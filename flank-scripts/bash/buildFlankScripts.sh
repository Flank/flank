#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

FLANK_SCRIPTS="$DIR/.."

"$FLANK_SCRIPTS/../gradlew" :flank-scripts:clean :flank-scripts:assemble :flank-scripts:shadowJar
cp "$FLANK_SCRIPTS"/build/libs/flank-scripts.jar "$DIR/flank-scripts.jar"
