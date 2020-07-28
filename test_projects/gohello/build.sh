#!/bin/bash

rm -rf "./bin/"
mkdir -p bin/win
mkdir -p bin/linux
mkdir -p bin/mac

GOOS=windows GOARCH=amd64 go build -o ./bin/win/gohello.exe
GOOS=linux   GOARCH=amd64 go build -o ./bin/linux/gohello
GOOS=darwin  GOARCH=amd64 go build -o ./bin/mac/gohello
