package ftl.run.platform.ios

import ftl.api.TestMatrixIos
import ftl.args.IosArgs
import ftl.client.google.GcToolResults
import ftl.run.platform.android.uploadAdditionalIpas
import ftl.run.platform.android.uploadOtherFiles

suspend fun createIosTestConfig(
    args: IosArgs
): TestMatrixIos.Config = TestMatrixIos.Config(
    clientDetails = args.clientDetails,
    networkProfile = args.networkProfile,
    directoriesToPull = args.directoriesToPull,
    testTimeout = args.testTimeout,
    recordVideo = args.recordVideo,
    flakyTestAttempts = args.flakyTestAttempts,
    failFast = args.failFast,
    project = args.project,
    resultsHistoryName = GcToolResults.createToolResultsHistory(args).historyId,
    devices = args.devices,
    otherFiles = args.uploadOtherFiles(),
    additionalIpasGcsPaths = args.uploadAdditionalIpas(),
)
