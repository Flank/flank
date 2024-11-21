# Google api use cases

## List of places where google api calls are used

## com.google.api.client.http

1. GoogleApiLogger.kt

## ftl.android

1. AndroidCatalog.kt

## ftl.args

1. ArgsHelper.kt

## ftl.config

1. Credentials.kt
1. FtlConstants.kt

## ftl.environment

1. ListIPBlocks.kt
1. ListLocales.kt
1. LocalesDescription.kt
1. NetworkProfileDescription.kt

## ftl.environment.android

1. AndroidModelDescription.kt
1. AndroidSoftwareVersionDescription.kt
1. ListAndroidDevices.kt
1. ListAndroidSoftwareVersions.kt

## ftl.environment.common

1. ListNetworkConfiguration.kt
1. ListOrientations.kt
1. ListProvidedSoftware.kt

## ftl.environment.ios

1. IosModelDescription.kt
1. IosSoftwareVersionDescription.kt
1. ListIOsDevices.kt
1. ListIOsSoftwareVersions.kt

## ftl.gc

1. GcAndroidDevice.kt
1. GcAndroidTestMatrix.kt
1. GcIosMatrix.kt
1. GcIosTestMatrix.kt
1. GcStorage.kt
1. GcTesting.kt
1. GcTestMatrix.kt
1. GcToolResults.kt
1. UserAuth.kt
1. Utils.kt

## ftl.gc.android

1. CreateAndroidInstrumentationTest.kt
1. CreateAndroidLoopTest.kt
1. CreateAndroidRobotTest.kt
1. SetupAndroidTest.kt
1. SetupEnvironmentVariables.kt
1. Utils.kt

## ftl.gc.ios

1. SetupIosTest.kt

## ftl.http

1. ExecuteWithRetry.kt
1. HttpTimeoutIncrease.kt

## ftl.ios

1. IosCatalog.kt

## ftl.json

1. MatrixMap.kt
1. OutcomeDetailsFormatter.kt
1. SavedMatrix.kt

## ftl.log

1. Loggers.kt

## ftl.mock

1. MockServer.kt

## ftl.reports

1. HtmlErrorReport.kt

## ftl.reports.api

1. CreateJUnitTestCase.kt
1. CreateJUnitTestResult.kt
1. CreateTestExecutionData.kt
1. CreateTestSuiteOverviewData.kt
1. PerformanceMetrics.kt
1. PrepareForJUnitResult.kt
1. ProcessFromApi.kt
1. Utils.kt

## ftl.reports.api.data

1. TestExecutionData.kt
1. TestSuiteOverviewData.kt

## ftl.reports.outcome

1. BillableMinutes.kt
1. CreateMatrixOutcomeSummary.kt
1. CreateTestSuiteOverviewData.kt
1. TestOutcome.kt
1. TestOutcomeContext.kt
1. Util.kt

## ftl.reports.util

1. ReportManager.kt

## ftl.run

1. RefreshLastRun.kt

## ftl.run.common

1. FetchArtifacts.kt
1. PollMatrices.kt
1. PrettyPrint.kt

## ftl.run.model

1. AndroidTestShards.kt

## ftl.run.platform

1. RunAndroidTests.kt

## ftl.run.platform.common

1. AfterRunTests.kt

## ftl.run.status

1. ExecutionStatusListPrinter.kt
1. TestMatrixStatusPrinter.kt

## ftl.util

1. MatrixState.kt
1. ObfuscationGson.kt
1. TestMatrixExtension.kt

## Gcloud Api Calls in Flank

1. GcToolResults.kt
    - createToolResultsHistory
    - getExecutionResult
    - getStepResult
    - getPerformanceMetric
    - listTestCases
    - getDefaultBucket
    - listAllEnvironments
    - listAllSteps

1. TestOutcomeContext.kt
    - fetchTestOutcomeContext

1. GcIosTestMatrix.kt
    - build

1. GcAndroidTestMatrix.kt
    - build

1. GcStorage.kt
    - uploadWithProgress
    - download
    - exist

1. Common environment information's
    - ListIPBlocks.kt
    - ListLocales.kt
    - LocalesDescription.kt
    - NetworkConfigurationCatalog.kt
    - NetworkProfileDescription.kt
    - ProvidedSoftwareCatalog.kt
    - TestEnvironmentInfo.kt

1. AndroidModelDescription.kt
1. AndroidSoftwareVersionDescription.kt
1. ListAndroidDevices.kt
1. ListAndroidSoftwareVersions.kt

1. IosModelDescription.kt
1. IosSoftwareVersionDescription.kt
1. ListIOsDevices.kt
1. ListIOsSoftwareVersions.kt

## Android Run use case

1. AndroidRunCommand.kt
    - L: 76 ```validate()```

1. ValidateAndroidArgs.kt
    - L: 18 ```AndroidArgs.validate()```
    - L: 20 ```AndroidArgs.assertDevicesSupported()```
        - L: 97 ```AndroidCatalog.supportedDeviceConfig()```
        - L: 104 ```AndroidCatalog.androidModelIds()```
        - L: 109 ```AndroidCatalog.androidVersionIds()```
        - L: 112 ```device.getSupportedVersionId()```
    - L: 32 ```IArgs.checkResultsDirUnique()```
    - ValidateCommonArgs.kt
        - L: 75 ```GcStorage.exist(resultsBucket, resultsDir)```

1. AndroidRunCommand.kt
    - L: 79 ```newTestRun()```

1. NewTestRun.kt
    - L: 27 ```runTests()```
    - L: 46 ```runAndroidTests()```

1. RunAndroidTests.kt
    - L: 46 ```uploadOtherFiles()```
    - UploadOtherFiles.kt
        - L: 14 ```GcStorage.upload()```

    - L: 47 ```uploadAdditionalApks()```
    - UploadApks.kt
        - L: 50 ```uploadAdditionalApks()```
        - L: 56 ```uploadToGcloudIfNeeded()```
        - FileReference.kt
            - L: 37 ```FileReference.uploadIfNeeded()```
            - L: 42 ```GcStorage.upload()```

    - L: 48 ```uploadObbFiles()```
    - UploadOtherFiles.kt
        - L: 18 ```uploadObbFiles()```
        - L: 20  ```GcStorage.upload()```

    - L: 49 ```createAndroidTestContexts()```
    - CreateAndroidTestContext.kt
        - L: 30 ```setupShards()```
        - L: 41 ```testContext.downloadApks()```
        - L: 50 ```app = app.downloadIfNeeded()```
        - L: 51 ```test = test.downloadIfNeeded()```
        - FileReference.kt
            - L: 29 ```GcStorage.download()```
            - GcStorage.kt
                - L: 179 ```download()```

    - L: 51 ```upload()```
    - UploadApks.kt
        - L: 23 ```context.upload()```
        - L: 26 ```AndroidTestContext.upload()```
        - L: 27 ```is InstrumentationTestContext -> upload()```
        - L: 33 ```InstrumentationTestContext.upload()```
        - L: 34 ```app.uploadIfNeeded()```
        - L: 35 ```test.uploadIfNeeded()```
        - FileReference.kt
            - L: 37 ```FileReference.uploadIfNeeded()```
            - L: 42 ```GcStorage.upload```
            - GcStorage.kt
                - L: 60 ```upload()```

    - L: 28 ```is RoboTestContext -> upload()```
    - L: 38 ```RoboTestContext.upload()```
    - L: 39 ```app.uploadIfNeeded()```
    - L: 40 ```roboScript.uploadIfNeeded()```
    - FileReference.kt
        - L: 37 ```FileReference.uploadIfNeeded()```
        - L: 42 ```GcStorage.upload```
        - GcStorage.kt
            - L: 60 ```upload()```

    - L: 29 ```is SanityRoboTestContext -> upload()```
    - L: 47 ```SanityRoboTestContext.upload()```
    - L: 48 ```app.uploadIfNeeded()```
    - FileReference.kt
        - L: 37 ```FileReference.uploadIfNeeded()```
        - L: 42 ```GcStorage.upload```
        - GcStorage.kt
            - L: 60 ```upload()```

    - L: 30 ```is GameLoopContext -> upload()```
    - L: 43 ```GameLoopContext.upload()```
    - L: 44 ```app.uploadIfNeeded()```
    - FileReference.kt
        - L: 37 ```FileReference.uploadIfNeeded()```
        - L: 42 ```GcStorage.upload```
        - GcStorage.kt
            - L: 60 ```upload()```

    - L: 59 ```GcAndroidTestMatrix.build()```
    - GcAndroidTestMatrix.kt
        - L: 30 ```build()```
        - L: 89 ```GcTesting.get.projects().testMatrices().create()```

1. NewTestRun.kt
    - L: 35 ```ReportManager.generate()```
    - ReportManager.kt
        - L: 97 ```parseTestSuite()```
        - L: 103 ```it.run()```
        - MatrixResultsReport.kt
            - L: 80 ```GcStorage.uploadReportResult()```
            - GcStorage.kt
                - L: 115 ```upload()```
        - CostReport.kt
            - L: 43 ```GcStorage.uploadReportResult()```
            - GcStorage.kt
                - L: 115 ```upload()```
        - L: 108 ```it.run()```
        - HtmlErrorReport.kt
            - L: 35 ```GcStorage.uploadReportResult()```
            - GcStorage.kt
                - L: 115 ```upload()```
        - L: 87 ```refreshMatricesAndGetExecutions()```
        - ProcessFromApi.kt
            - L: 13 ```refreshMatricesAndGetExecutions()```
            - L: 18 ```refreshTestMatrices()```
            - L: 24 ```cTestMatrix.refresh()```

        - L: 121 ```refreshMatricesAndGetExecutions()```
        - ProcessFromApi.kt
            - L: 13 ```refreshMatricesAndGetExecutions()```
            - L: 18 ```refreshTestMatrices()```
            - L: 24 ```cTestMatrix.refresh()```

        - L: 123 ```createAndUploadPerformanceMetricsForAndroid()```
        - L: 148 ```getAndUploadPerformanceMetrics()```
        - PerformanceMetrics.kt
            - L: 16 ```getAndUploadPerformanceMetrics()```
            - L: 27 ```getPerformanceMetric()```
            - L: 45 ```GcToolResults.getPerformanceMetric()```
            - L: 25 ```performanceMetrics.upload()```
            - L: 47 ```GcStorage.uploadPerformanceMetrics()```

        - L: 124 ```GcStorage.uploadMatricesId()```
        - GcStorage.kt
            - L: 96 ```uploadMatricesId()```
            - L: 60 ```upload()```

## iOS Run use case

1. IosRunCommand.kt
    - L: 76 ```validate()```

1. ValidateIosArgs.kt
    - L: 14 ```IosArgs.validate()```
    - L: 16 ```assertXcodeSupported()```
    - L: 79 ```IosCatalog.supportedXcode()```
    - IosCatalog.kt
        - L: 44 ```xcodeVersions()```
        - L: 47 ```iosDeviceCatalog()```
        - L: 63: ```GcTesting...iosDeviceCatalog()```

    - L: 17 ```assertDevicesSupported(()```
    - L: 84 ```IosCatalog.supportedDevice()```
    - IosCatalog.kt
        - L: 53 ```iosDeviceCatalog()```
        - L: 63 ```GcTesting...iosDeviceCatalog()```
    - L: 21 ```checkResultsDirUnique()```
    - ValidateCommonArgs.kt
        - L: 75 ```GcStorage.exist()```

1. IosRunCommand.kt
    - L: 78 ```newTestRun()```

1. NewTestRun.kt
    - L: 27 ```runTests()```
    - L: 47 ```runIosTests()```

1. RunIosTests.kt
    - L: 41 ```uploadOtherFiles()```
    - UploadOtherFiles.kt
        - L: 14 ```GcStorage.upload()```
    - L: 42 ```uploadAdditionalIpas()```
    - UploadApks.kt
        - L: 54 ```uploadToGcloudIfNeeded()```
        - L: 61 ```uploadIfNeeded()```
        - FileReference.kt
            - L: 42 ```GcStorage.upload()```
    - L: 49 ```createIosTestContexts()```
    - CreateIosTestContext.kt
        - L: 9 ```createXcTestContexts()```
        - CreateXcTestContext.kt
            - L: 17 ```uploadIfNeeded()```
            - FileReference.kt
                - L: 32 ```uploadIfNeeded()```
                - L: 42: ```GcStorage.upload()```
            - L: 28 ```GcStorage.uploadXCTestFile()```
            - GcStorage.kt
                - L: 124 ```upload()```
            - L: 10 ```createGameloopTestContexts()```
            - CreateGameloopTestContext.kt
                - L: 15 ```uploadIfNeeded()```
                - FileReference.kt
                    - L: 32 ```uploadIfNeeded()```
                    - L: 42 ```GcStorage.upload()```
    - L: 50 ```GcIosTestMatrix.build()```
    - GcIosTestMatrix.kt
        - L: 66 ```GcTesting...create()```

1. NewTestRun.kt
    - L: 35 ```ReportManager.generate()```
    - ReportManager.kt
        - L: 103 ```it.run()```
        - MatrixResultsReport.kt
            - L: 80 ```GcStorage.uploadReportResult()```
            - GcStorage.kt
                - L: 115 ```upload()```
        - CostReport.kt
            - L: 43 ```GcStorage.uploadReportResult()```
            - GcStorage.kt
                - L: 115 ```upload()```
        - L: 108 ```it.run()```
        - HtmlErrorReport.kt
            - L: 35 ```GcStorage.uploadReportResult()```
            - GcStorage.kt
                - L: 115 ```upload()```
        - L: 121 ```refreshMatricesAndGetExecutions()```
        - ProcessFromApi.kt
            - L: 13 ```refreshMatricesAndGetExecutions()```
            - L: 18 ```refreshTestMatrices()```
            - L: 24 ```cTestMatrix.refresh()```

        - L: 123 ```createAndUploadPerformanceMetricsForAndroid()```
        - L: 148 ```getAndUploadPerformanceMetrics()```
        - PerformanceMetrics.kt
            - L: 16 ```getAndUploadPerformanceMetrics()```
            - L: 27 ```getPerformanceMetric()```
            - L: 45 ```GcToolResults.getPerformanceMetric()```
            - L: 25 ```performanceMetrics.upload()```
            - L: 47 ```GcStorage.uploadPerformanceMetrics()```

        - L: 124 ```GcStorage.uploadMatricesId()```
        - GcStorage.kt
            - L: 96 ```uploadMatricesId()```
            - L: 60 ```upload()```
