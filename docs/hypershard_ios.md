# Hypershard iOS

This is a step-by-step guide of using Hypershard for companies that are unable to use symbol table dumping on iOS.

### Installation

1. Clone GitHub repository using
    1. https - `git clone https://github.com/dropbox/hypershard-ios.git`
    2. SSH - `git clone git@github.com:dropbox/hypershard-ios.git`
    3. GitHub CLI - `gh repo clone dropbox/hypershard-ios`
2. Install swift if you are not using macOS
    1. https://swift.org/download/#releases
3. Open Terminal/cmd
4. Change working directory to the path where you clone `hypershard-ios`
5. Build hyper shard-ios with command `swift build -c release`
6. It will build the binary into the `.build` folder
7. The resulting binary will be placed in the `.build/release/hypershard`.


### Running

1. Open Terminal/cmd

2. change the working directory to `.build/release` or add it to `PATH`

3. To run hypershard make CLI invocation with the command:

   ```bash
   hypershard TEST_TARGET_NAME ROOT_PATH
   ```

   where

    - `TEST_TARGET_NAME` - the name of the Xcode test target containing the UI tests
    - `ROOT_PATH` - either a path where all the `XCUITest`s classes are stored, or the path of the Xcode project containing `TEST_TARGET_NAME`

   for example based on Flank test project

   ```bash
   ./hypershard EarlGreyExample test_projects/ios/EarlGreyExample/EarlGreyExampleSwiftTests
   ```

4. The output will be printed to console

   ```json
   {"path":"","phase":"XCUI test shard","env":{},"cmd":"","tests":["EarlGreyExample.EarlGreyExampleSwiftTests.testThatThrows","EarlGreyExample.EarlGreyExampleSwiftTests.testBasicSelection","EarlGreyExample.EarlGreyExampleSwiftTests.testBasicSelectionAndAction","EarlGreyExample.EarlGreyExampleSwiftTests.testBasicSelectionAndAssert","EarlGreyExample.EarlGreyExampleSwiftTests.testBasicSelectionActionAssert","EarlGreyExample.EarlGreyExampleSwiftTests.testSelectionOnMultipleElements","EarlGreyExample.EarlGreyExampleSwiftTests.testCollectionMatchers","EarlGreyExample.EarlGreyExampleSwiftTests.testWithInRoot","EarlGreyExample.EarlGreyExampleSwiftTests.testWithCustomMatcher","EarlGreyExample.EarlGreyExampleSwiftTests.testTableCellOutOfScreen","EarlGreyExample.EarlGreyExampleSwiftTests.testCatchErrorOnFailure","EarlGreyExample.EarlGreyExampleSwiftTests.testCustomAction","EarlGreyExample.EarlGreyExampleSwiftTests.testWithCustomAssertion","EarlGreyExample.EarlGreyExampleSwiftTests.testWithCustomFailureHandler","EarlGreyExample.EarlGreyExampleSwiftTests.testLayout","EarlGreyExample.EarlGreyExampleSwiftTests.testWithCondition","EarlGreyExample.EarlGreyExampleSwiftTests.testWithGreyAssertions"]}
   ```


5. If you would like to store the output to a file just add a path to the file with `--path` option

   For example:

   ```bash
   ./hypershard EarlGreyExample test_projects/ios/EarlGreyExample/EarlGreyExampleSwiftTests --path results.json
   ```


### Resources

https://github.com/dropbox/hypershard-ios
