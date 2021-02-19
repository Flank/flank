# Investigation of Google API usage [WIP]

## References

Based on
* [google_api_usecases](./google_api_usecases.md)

## Full list

1. com.google.api.client.http
   * GoogleApiLogger.kt
1. ftl.analytics
   * PrepareConfiguration.kt ?
   * UsageStatisticsClient.kt
1. ftl.android
   * AndroidCatalog.kt
1. ftl.args
   * AndroidArgsCompanion.kt ?
   * ArgsHelper.kt
   * IosArgs.kt ?
   * IosArgsCompanion.kt ?
1. ftl.args.yml
   * YamlDeprecated.kt ?
1. ftl.config
   * Credentials.kt ?
   * FtlConstants.kt
1. ftl.doctor
   * Doctor.kt ?
1. ftl.environment
   * ListIPBlocks.kt $
   * ListLocales.kt $
   * LocalesDescription.kt $
   * NetworkProfileDescription.kt $
1. ftl.environment.android
   * AndroidModelDescription.kt $
   * AndroidSoftwareVersionDescription.kt $
   * ListAndroidDevices.kt $
   * ListAndroidSofwareVersions.kt $
1. ftl.environment.common
   * ListNetworkConfiguration.kt $
   * ListOrientations.kt $
   * ListProvidedSoftware.kt $
1. ftl.environment.ios
   * IosModelDescription.kt $
   * IosSoftwareVersionDescription.kt $
   * ListIOsDevices.kt $
   * ListIOsSofwareVersions.kt $
1. ftl.gc
   * GcAndroidDevice.kt $
   * GcAndroidTestMatrix.kt
   * GcIosMatrix.kt $
   * GcIosTestMatrix.kt
   * GcStorage.kt
   * GcTesting.kt $
   * GcTestMatrix.kt
   * GcToolResults.kt
   * UserAuth.kt
   * ftl/gc/Utils.kt $
1. ftl.gc.android
   * CreateAndroidInstrumentationTest.kt $
   * CreateAndroidLoopTest.kt $
   * CreateAndroidRobotTest.kt $
   * SetupAndroidTest.kt $
   * SetupEnvironmentVariables.kt $
   * ftl/gc/android/Utils.kt $
1. ftl.gc.ios
   * SetupIosTest.kt $
1. ftl.http
   * ExecuteWithRetry.kt
   * HttpTimeoutIncrease.kt
1. ftl.ios
   * IosCatalog.kt $
1. ftl.ios.xctest.common
   * FindTestsForTarget.kt ?
1. ftl.json
   * MatrixMap.kt $
   * OutcomeDetailsFormatter.kt $
   * SavedMatrix.kt $
1. ftl.log
   * Loggers.kt
1. ftl.mock
   * MockServer.kt
1. ftl.reports
   * HtmlErrorReport.kt ?
1. ftl.reports.api
   * CreateJUnitTestCase.kt $
   * CreateJUnitTestResult.kt $
   * CreateTestExecutionData.kt
   * CreateTestSuiteOverviewData.kt $
   * PerformanceMetrics.kt
   * PrepareForJUnitResult.kt
   * ProcessFromApi.kt $
   * ftl/reports/api/Utils.kt
1. ftl.reports.api.data
   * TestExecutionData.kt $
   * TestSuiteOverviewData.kt $
1. ftl.reports.outcome
   * BillableMinutes.kt $
   * CreateMatrixOutcomeSummary.kt $
   * CreateTestSuiteOverviewData.kt $
   * TestOutcome.kt $
   * TestOutcomeContext.kt $
   * ftl/reports/outcome/Util.kt $
1. ftl.reports.util
   * ReportManager.kt $
1. ftl.run
   * RefreshLastRun.kt $
1. ftl.run.common
   * FetchArtifacts.kt
   * PollMatrices.kt
   * PrettyPrint.kt ?
1. ftl.run.model
   * AndroidTestShards.kt ?
1. ftl.run.platform
   * RunAndroidTests.kt $
1. ftl.run.platform.android
   * CreateAndroidTestContext.kt ?
   * ResolveApks.kt ?
1. ftl.run.platform.common
   * AfterRunTests.kt $
1. ftl.run.status
   * ExecutionStatusListPrinter.kt $
   * ExecutionStatusPrinter.kt ?
   * TestMatrixStatusPrinter.kt $
1. ftl.util
   * LogTableBuilder.kt ?
   * MatrixState.kt $
   * ObfuscationGson.kt ?
   * TestMatrixExtension.kt $

where
* `?` - probably is not part of data, need investigation
* `$` - only operates on API structures, not call methods directly


## Packages

1. com.google.api.client.http
1. ftl.analytics
1. ftl.android
1. ftl.args
1. ftl.args.yml
1. ftl.config
1. ftl.doctor
1. ftl.environment
1. ftl.environment.android
1. ftl.environment.common
1. ftl.environment.ios
1. ftl.gc
1. ftl.gc.android
1. ftl.gc.ios
1. ftl.http
1. ftl.ios
1. ftl.ios.xctest.common
1. ftl.json
1. ftl.log
1. ftl.mock
1. ftl.reports
1. ftl.reports.api
1. ftl.reports.api.data
1. ftl.reports.outcome
1. ftl.reports.util
1. ftl.run
1. ftl.run.common
1. ftl.run.model
1. ftl.run.platform
1. ftl.run.platform.android
1. ftl.run.platform.common
1. ftl.run.status
1. ftl.util

## Files

1. GoogleApiLogger.kt
1. PrepareConfiguration.kt
1. UsageStatisticsClient.kt
1. AndroidCatalog.kt
1. AndroidArgsCompanion.kt
1. ArgsHelper.kt
1. IosArgs.kt
1. IosArgsCompanion.kt
1. YamlDeprecated.kt
1. Credentials.kt
1. FtlConstants.kt
1. Doctor.kt
1. ListIPBlocks.kt
1. ListLocales.kt
1. LocalesDescription.kt
1. NetworkProfileDescription.kt
1. AndroidModelDescription.kt
1. AndroidSoftwareVersionDescription.kt
1. ListAndroidDevices.kt
1. ListAndroidSofwareVersions.kt
1. ListNetworkConfiguration.kt
1. ListOrientations.kt
1. ListProvidedSoftware.kt
1. IosModelDescription.kt
1. IosSoftwareVersionDescription.kt
1. ListIOsDevices.kt
1. ListIOsSofwareVersions.kt
1. GcAndroidDevice.kt
1. GcAndroidTestMatrix.kt
1. GcIosMatrix.kt
1. GcIosTestMatrix.kt
1. GcStorage.kt
1. GcTesting.kt
1. GcTestMatrix.kt
1. GcToolResults.kt
1. UserAuth.kt
1. ftl/gc/android/Utils.kt
1. CreateAndroidInstrumentationTest.kt
1. CreateAndroidLoopTest.kt
1. CreateAndroidRobotTest.kt
1. SetupAndroidTest.kt
1. SetupEnvironmentVariables.kt
1. ftl/gc/android/Utils.kt
1. SetupIosTest.kt
1. ExecuteWithRetry.kt
1. HttpTimeoutIncrease.kt
1. IosCatalog.kt
1. FindTestsForTarget.kt
1. MatrixMap.kt
1. OutcomeDetailsFormatter.kt
1. SavedMatrix.kt
1. Loggers.kt
1. MockServer.kt
1. HtmlErrorReport.kt
1. CreateJUnitTestCase.kt
1. CreateJUnitTestResult.kt
1. CreateTestExecutionData.kt
1. CreateTestSuiteOverviewData.kt
1. PerformanceMetrics.kt
1. PrepareForJUnitResult.kt
1. ProcessFromApi.kt
1. ftl/reports/api/Utils.kt
1. TestExecutionData.kt
1. TestSuiteOverviewData.kt
1. BillableMinutes.kt
1. CreateMatrixOutcomeSummary.kt
1. CreateTestSuiteOverviewData.kt
1. TestOutcome.kt
1. TestOutcomeContext.kt
1. ftl/reports/outcome/Util.kt
1. ReportManager.kt
1. RefreshLastRun.kt
1. FetchArtifacts.kt
1. PollMatrices.kt
1. PrettyPrint.kt
1. AndroidTestShards.kt
1. RunAndroidTests.kt
1. CreateAndroidTestContext.kt
1. ResolveApks.kt
1. AfterRunTests.kt
1. ExecutionStatusListPrinter.kt
1. ExecutionStatusPrinter.kt
1. TestMatrixStatusPrinter.kt
1. LogTableBuilder.kt
1. MatrixState.kt
1. ObfuscationGson.kt
1. TestMatrixExtension.kt