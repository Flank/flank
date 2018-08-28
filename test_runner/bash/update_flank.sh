#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

FLANK="$DIR/.."

"$FLANK/gradlew" -p "$FLANK" clean assemble fatJar

cp "$FLANK"/build/libs/flank-*.jar "$DIR/flank.jar"
