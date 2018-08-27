#!/usr/bin/env bash

# npm -g install sort-json

# Note: API discovery JSON is out of date. Check the gcloud CLI repo for most recent JSON.
# https://github.com/bootstraponline/gcloud_cli/blob/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/testing_v1.json

TOOL_RESULTS=toolresults_v1beta3.json
rm "$TOOL_RESULTS"
curl -o "$TOOL_RESULTS" https://www.googleapis.com/discovery/v1/apis/toolresults/v1beta3/rest
sort-json "$TOOL_RESULTS"

TESTING=testing_v1.json
rm "$TESTING"
curl -o "$TESTING" https://www.googleapis.com/discovery/v1/apis/testing/v1/rest
sort-json "$TESTING"
