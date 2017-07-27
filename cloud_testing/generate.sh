#!/bin/bash

# Note: Must have already installed google-apis-client-generator from the master branch. PIP release will not work!

pushd apis

# Generate only the testing library since the others are published officially already.

#  generate_library \
#     --input=./storage_v1.json \
#     --language=java \
#     --output_dir=./storage

 generate_library \
    --input=./testing_v1.json \
    --language=java \
    --output_dir=./testing

#  generate_library \
#     --input=./toolresults_v1beta3.json \
#     --language=java \
#     --output_dir=./apis/toolresults

popd
