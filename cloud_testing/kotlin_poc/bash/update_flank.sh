#!/usr/bin/env bash

DIR=`dirname "$BASH_SOURCE"`

FLANK="$DIR/.."

"$FLANK/gradlew" -p "$FLANK" clean fatJar

cp "$FLANK/build/libs/kotlin_poc-all-1.0-SNAPSHOT.jar" "$DIR/flank.jar"
