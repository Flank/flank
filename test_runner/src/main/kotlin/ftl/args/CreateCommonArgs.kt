package ftl.args

import ftl.config.CommonConfig
import ftl.run.status.OutputStyle
import ftl.run.status.asOutputStyle
import ftl.util.uniqueObjectName

fun CommonConfig.createCommonArgs(
    data: String
) = CommonArgs(
    data = data,

    // gcloud
    devices = gcloud.devices!!,
    resultsBucket = ArgsHelper.createGcsBucket(
        projectId = flank.project!!,
        bucket = gcloud.resultsBucket!!
    ),
    resultsDir = gcloud.resultsDir ?: uniqueObjectName(),
    recordVideo = gcloud.recordVideo!!,
    testTimeout = gcloud.timeout!!,
    async = gcloud.async!!,
    resultsHistoryName = gcloud.resultsHistoryName,
    flakyTestAttempts = gcloud.flakyTestAttempts!!,
    networkProfile = gcloud.networkProfile,
    clientDetails = gcloud.clientDetails,

    // flank
    maxTestShards = flank.maxTestShards!!,
    shardTime = flank.shardTime!!,
    repeatTests = flank.repeatTests!!,
    smartFlankGcsPath = flank.smartFlankGcsPath!!,
    smartFlankDisableUpload = flank.smartFlankDisableUpload!!,
    testTargetsAlwaysRun = flank.testTargetsAlwaysRun!!,
    runTimeout = flank.runTimeout!!,
    fullJUnitResult = flank.fullJUnitResult!!,
    project = flank.project!!,
    outputStyle = outputStyle,
    keepFilePath = flank.keepFilePath!!,
    ignoreFailedTests = flank.ignoreFailedTests!!,
    filesToDownload = flank.filesToDownload!!,
    disableSharding = flank.disableSharding!!,
    localResultDir = flank.localResultsDir!!,
    disableResultsUpload = flank.disableResultsUpload!!,
    defaultTestTime = flank.defaultTestTime!!,
    defaultClassTestTime = flank.defaultClassTestTime!!,
    useAverageTestTimeForNewTests = flank.useAverageTestTimeForNewTests!!
).apply {
    ArgsHelper.createJunitBucket(project, smartFlankGcsPath)
}

private val CommonConfig.outputStyle
    get() = flank.outputStyle
        ?.asOutputStyle()
        ?: defaultOutputStyle

private val CommonConfig.defaultOutputStyle
    get() = if (hasMultipleExecutions)
        OutputStyle.Multi else
        OutputStyle.Verbose

private val CommonConfig.hasMultipleExecutions
    get() = gcloud.flakyTestAttempts!! > 0 ||
            (!flank.disableSharding!! && flank.maxTestShards!! > 0)
