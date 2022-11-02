# Flank run refactor [Not complete]

# References

* `https://github.com/Flank/flank/issues/1317`

# Motivation

Flank run is getting bigger over the time.
Currently, the amount of code related to test run is large,
so it could be hard to understand and keep in mind the whole process.
To make the work more convenient, faster and scalable, we should re-think
and reorganize the code between packages and files.

# Goals

* `All logic related to flank run grouped in one root package`
* `Package structure flat as possible.`
* `Flank run process simplified to sequence of synchronous atomic steps.`
* `Packages and code are easy to identify as steps.`

# Non-Goals

* `Refactor code related to other commands than flank run`

# Design

> Explain and diagram the technical design
>
> Identify risks and edge cases

# API

> What will the proposed API look like?

# Results

> What was the outcome of the project?

# Dependencies

> What is the project blocked on?

> What will be impacted by the project?

## Files

## ftl/run

```
ftl/run/
├── CancelLastRun.kt
├── DumpShards.kt
├── NewTestRun.kt
├── RefreshLastRun.kt
├── common
│   ├── FetchArtifacts.kt
│   ├── GetLastArgs.kt
│   ├── GetLastGcsPath.kt
│   ├── GetLastMatrices.kt
│   ├── PollMatrices.kt
│   ├── PrettyPrint.kt
│   └── UpdateMatrixFile.kt
├── exception
│   ├── ExceptionHandler.kt
│   ├── FlankException.kt
│   └── FlankExitCodes.kt
├── model
│   ├── AndroidMatrixTestShards.kt
│   ├── AndroidTestContext.kt
│   ├── AndroidTestShards.kt
│   ├── IosTestContext.kt
│   └── TestResult.kt
├── platform
│   ├── RunAndroidTests.kt
│   ├── RunIosTests.kt
│   ├── android
│   │   ├── AndroidTestConfig.kt
│   │   ├── CreateAndroidLoopConfig.kt
│   │   ├── CreateAndroidTestConfig.kt
│   │   ├── CreateAndroidTestContext.kt
│   │   ├── CreateInstrumentationConfig.kt
│   │   ├── CreateRoboConfig.kt
│   │   ├── GetAndroidMatrixShards.kt
│   │   ├── ResolveApks.kt
│   │   ├── UploadApks.kt
│   │   └── UploadOtherFiles.kt
│   ├── common
│   │   ├── AfterRunTests.kt
│   │   ├── BeforeRunMessage.kt
│   │   └── BeforeRunTests.kt
│   └── ios
│       ├── CreateGameloopTestContext.kt
│       ├── CreateIosTestContext.kt
│       └── CreateXcTestContext.kt
└── status
    ├── ExecutionStatus.kt
    ├── ExecutionStatusListPrinter.kt
    ├── ExecutionStatusPrinter.kt
    ├── OutputStyle.kt
    └── TestMatrixStatusPrinter.kt


```

## Dependencies

* `ftl/args/AndroidArgs.kt`
* `ftl/args/AndroidArgsCompanion.kt`
* `ftl/args/ArgsHelper.kt`
* `ftl/args/CalculateShardsResult.kt`
* `ftl/args/FlankRoboDirective.kt`
* `ftl/args/IArgs.kt`
* `ftl/args/IgnoredTestCases.kt`
* `ftl/args/IosArgs.kt`
* `ftl/args/IosArgsCompanion.kt`
* `ftl/args/ShardChunks.kt`
* `ftl/args/ValidateAndroidArgs.kt`
* `ftl/args/ValidateIosArgs.kt`
* `ftl/args/yml/AppTestPair.kt`
* `ftl/config/FtlConstants.kt`
* `ftl/filter/TestFilters.kt`
* `ftl/gc/GcAndroidDevice.kt`
* `ftl/gc/GcAndroidTestMatrix.kt`
* `ftl/gc/GcIosMatrix.kt`
* `ftl/gc/GcIosTestMatrix.kt`
* `ftl/gc/GcStorage.kt`
* `ftl/gc/GcTesting.kt`
* `ftl/gc/GcTestMatrix.kt`
* `ftl/gc/GcToolResults.kt`
* `ftl/http/ExecuteWithRetry.kt`
* `ftl/ios/xctest/XcTestData.kt`
* `ftl/ios/xctest/XcTestRunFlow.kt`
* `ftl/ios/xctest/common/Util.kt`
* `ftl/json/MatrixMap.kt`
* `ftl/json/SavedMatrix.kt`
* `ftl/reports/output/OutputReport.kt`
* `ftl/reports/output/OutputReportLoggers.kt`
* `ftl/reports/util/ReportManager.kt`
* `ftl/shard/Chunk.kt`
* `ftl/shard/Shard.kt`
* `ftl/shard/TestCasesCreator.kt`
* `ftl/util/Artifacts.kt`
* `ftl/util/FileReference.kt`
* `ftl/util/FlankTestMethod.kt`
* `ftl/util/FlowExt.kt`
* `ftl/util/MatrixState.kt`
* `ftl/util/ObfuscationGson.kt`
* `ftl/util/ShardCounter.kt`
* `ftl/util/StopWatch.kt`
* `ftl/util/TestMatrixExtension.kt`


# Testing

> How will the project be tested?

# Alternatives Considered [optional]

> Summarize alternative designs (pros & cons)

# Timeline [optional for regular tigers]

> Document milestones and deadlines.

DONE:

  -

NEXT:

  -
