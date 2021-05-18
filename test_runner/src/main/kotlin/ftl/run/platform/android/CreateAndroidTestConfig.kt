package ftl.run.platform.android

import ftl.api.TestMatrixAndroid
import ftl.args.AndroidArgs
import ftl.args.isDontAutograntPermissions
import ftl.client.google.GcToolResults

suspend fun createAndroidTestConfig(
    args: AndroidArgs
): TestMatrixAndroid.Config = TestMatrixAndroid.Config(
    clientDetails = args.clientDetails,
    resultsBucket = args.resultsBucket,
    autoGoogleLogin = args.autoGoogleLogin,
    networkProfile = args.networkProfile,
    directoriesToPull = args.directoriesToPull,
    obbNames = args.obbNames,
    obbFiles = args.uploadObbFiles(),
    environmentVariables = args.environmentVariables,
    autoGrantPermissions = args.isDontAutograntPermissions.not(),
    testTimeout = args.testTimeout,
    performanceMetrics = args.performanceMetrics,
    recordVideo = args.recordVideo,
    flakyTestAttempts = args.flakyTestAttempts,
    failFast = args.failFast,
    project = args.project,
    resultsHistoryName = GcToolResults.createToolResultsHistory(args).historyId,
    otherFiles = args.uploadOtherFiles(),
    resultsDir = args.resultsDir,
    devices = args.devices,
    additionalApkGcsPaths = args.uploadAdditionalApks(),
    repeatTests = args.repeatTests
)
