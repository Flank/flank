#!/bin/bash
@echo off
echo Changing git config for hooks to .githooks
git config --local core.hooksPath .githooks/
echo Complete!