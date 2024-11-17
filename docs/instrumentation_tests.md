# Flank instrumentation tests

Flank contains a project for instrumentation tests placed on flank_tests directory.

Tests can be run with Gradle wrapper and parametrized by command-line arguments

## Commands

1. ```flank-path```  location of flank.jar
2. ```yml-path``` location of test yml
3. ```run-params``` optional additional run parameters, default parameters depend on the platform
   1. for iOS is ```firebase, test, ios, run```
   2. for android ```firebase, test, ios, run```
4. ```working-directory``` optional parameter for set working directory default is: ```./```
5. ```output-pattern``` optional parameter for set regex to compare output
6. ```expected-output-code``` optional parameter for verify output code default: 0

## Example of run android test

```./gradlew test --tests IntegrationTests.shouldMatchAndroidSuccessExitCodeAndPattern -Dflank-path=../test_runner/build/libs/flank.jar -Dyml-path=./src/test/resources/flank_android.yml```

## Example of run ios test

```./gradlew test --tests IntegrationTests.shouldMatchIosSuccessExitCodeAndPattern -Dflank-path=../test_runner/build/libs/flank.jar -Dyml-path=./src/test/resources/flank_ios.yml```
