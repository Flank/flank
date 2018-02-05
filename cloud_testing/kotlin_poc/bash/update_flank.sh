#!/usr/bin/env bash

FLANK="../../../flank/cloud_testing/kotlin_poc/"
pushd "$FLANK"
./gradlew clean fatJar
popd

cp "$FLANK/build/libs/kotlin_poc-all-1.0-SNAPSHOT.jar" ./flank.jar
