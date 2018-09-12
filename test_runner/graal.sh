#!/bin/bash

./gradlew clean assemble fatJar

native-image \
-jar ./build/libs/flank-3.0-SNAPSHOT.jar \
-H:ReflectionConfigurationFiles=graal_reflectconfig.json
