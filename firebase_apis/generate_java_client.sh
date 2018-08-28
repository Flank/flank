#!/bin/bash

# Note: Must have already installed google-apis-client-generator from the master branch. PIP release will not work!

# git clone https://github.com/google/apis-client-generator.git
# pip install .

# Generate only the testing library since the others are published officially already.

#  generate_library \
#     --input=./storage_v1.json \
#     --language=java \
#     --output_dir=./storage

rm -rf "./test_api/src/"

 generate_library \
    --input=./json/testing_v1.json \
    --language=java \
    --package_path=api/services \
    --output_dir=./test_api/src/main/java

mv ./test_api/src/main/java/pom.xml ./test_api/pom.xml

#  generate_library \
#     --input=./toolresults_v1beta3.json \
#     --language=java \
#     --output_dir=./apis/toolresults

popd
