@echo off
echo Changing git config for hooks to .githooks
call git config --local core.hooksPath .githooks/
echo Complete!
