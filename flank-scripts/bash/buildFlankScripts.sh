#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

FLANK_SCRIPTS="$DIR/.."

"$FLANK_SCRIPTS/gradlew" -p "$FLANK_SCRIPTS" clean assemble shadowJar
cp "$FLANK_SCRIPTS"/build/libs/flankScripts.jar "$DIR/flankScripts.jar"
