#!/usr/bin/env bash

echo 'Attempting to sendMessage'
cd /
./sendMessage "$INPUT_XOXCTOKEN" "$INPUT_CHANNEL" "$INPUT_MESSAGE" "$INPUT_COOKIE"

echo 'Sending message done!'
