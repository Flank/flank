#!/usr/bin/env bash

echo 'Sending message'
./sendMessage $INPUT_XOXCTOKEN $INPUT_MESSAGE $INPUT_CHANNEL $INPUT_COOKIE
echo 'Sending message done!'
