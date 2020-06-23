package ftl.args

import ftl.config.CommonConfig
import ftl.run.status.OutputStyle
import ftl.run.status.asOutputStyle
import ftl.util.uniqueObjectName

fun CommonConfig.createCommonArgs(
    data: String,
    hasMultipleExecutions: Boolean = hasMultipleExecutions()
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
    maxTestShards = convertToShardCount(flank.maxTestShards!!),
    shardTime = flank.shardTime!!,
    repeatTests = flank.repeatTests!!,
    smartFlankGcsPath = flank.smartFlankGcsPath!!,
    smartFlankDisableUpload = flank.smartFlankDisableUpload!!,
    testTargetsAlwaysRun = flank.testTargetsAlwaysRun!!,
    runTimeout = flank.runTimeout!!,
    fullJUnitResult = flank.fullJUnitResult!!,
    project = flank.project!!,
    outputStyle = outputStyle(hasMultipleExecutions),
    keepFilePath = flank.keepFilePath!!,
    ignoreFailedTests = flank.ignoreFailedTests!!,
    filesToDownload = flank.filesToDownload!!,
    disableSharding = flank.disableSharding!!,
    localResultDir = flank.localResultsDir!!
).apply {
    ArgsHelper.createJunitBucket(project, smartFlankGcsPath)
}

fun CommonConfig.outputStyle(hasMultipleExecutions: Boolean) =
    flank.outputStyle
        ?.asOutputStyle()
        ?: defaultOutputStyle(hasMultipleExecutions)

fun defaultOutputStyle(hasMultipleExecutions: Boolean) =
    if (hasMultipleExecutions)
        OutputStyle.Single else
        OutputStyle.Verbose

fun CommonConfig.hasMultipleExecutions(): Boolean =
    gcloud.flakyTestAttempts!! > 0 ||
            (!flank.disableSharding!! && flank.maxTestShards!! > 0)

private fun convertToShardCount(inputValue: Int): Int =
    if (inputValue != -1)
        inputValue else
        IArgs.AVAILABLE_SHARD_COUNT_RANGE.last
