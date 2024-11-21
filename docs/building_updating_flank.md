# Building and Updating Flank
Ensure that you have followed all steps for [contributing and building Flank](../README.md)

## Building an updated flank
To build an updated version of flank from source simply run (This assumes you are in the root Flank directory)

> ./gradlew flankFullRun

The flank full run task, builds a clean Flan, runs all tests and runs the updateFlank gradle task.


This will create the `Flank.jar` file and place it in `/test_runner/bash`

## Building a minimized and optimized version of Flank (Proguard)
To build a proguarded version of Flank
> ./gradlew applyProguard

This will generate a second `Flank.jar`, named `Flank-proguard.jar` found at `/test_runner/bash`

To make use of this jar copy and rename it to `Flank.jar`



