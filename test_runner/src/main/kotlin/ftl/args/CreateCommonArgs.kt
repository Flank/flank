package ftl.args

import ftl.args.yml.toType
import ftl.config.CommonConfig
import ftl.reports.output.OutputReportType
import ftl.run.status.OutputStyle
import ftl.run.status.asOutputStyle
import ftl.util.require
import ftl.util.uniqueObjectName

fun CommonConfig.createCommonArgs(
    data: String
) = CommonArgs(
    data = data,

    // gcloud
    devices = gcloud::devices.require(),
    resultsBucket = ArgsHelper.createGcsBucket(
        projectId = flank::project.require().toLowerCase(),
        bucket = gcloud::resultsBucket.require()
    ),
    resultsDir = gcloud.resultsDir ?: uniqueObjectName(),
    recordVideo = gcloud::recordVideo.require(),
    testTimeout = gcloud::timeout.require(),
    async = gcloud::async.require(),
    resultsHistoryName = gcloud.resultsHistoryName,
    flakyTestAttempts = gcloud::flakyTestAttempts.require(),
    networkProfile = gcloud.networkProfile,
    clientDetails = gcloud.clientDetails,
    directoriesToPull = gcloud::directoriesToPull.require(),
    otherFiles = gcloud::otherFiles.require().mapValues { (_, path) -> path.normalizeFilePath() },
    scenarioNumbers = gcloud::scenarioNumbers.require(),
    type = gcloud.type?.toType(),
    failFast = gcloud::failFast.require(),

    // flank
    maxTestShards = flank::maxTestShards.require(),
    shardTime = flank::shardTime.require(),
    repeatTests = flank::repeatTests.require(),
    smartFlankGcsPath = flank::smartFlankGcsPath.require(),
    smartFlankDisableUpload = flank::smartFlankDisableUpload.require(),
    testTargetsAlwaysRun = flank::testTargetsAlwaysRun.require(),
    runTimeout = flank::runTimeout.require(),
    fullJUnitResult = flank::fullJUnitResult.require(),
    project = flank::project.require().toLowerCase(),
    outputStyle = outputStyle,
    keepFilePath = flank::keepFilePath.require(),
    ignoreFailedTests = flank::ignoreFailedTests.require(),
    filesToDownload = flank::filesToDownload.require(),
    disableSharding = flank::disableSharding.require(),
    localResultDir = flank::localResultsDir.require(),
    disableResultsUpload = flank::disableResultsUpload.require(),
    defaultTestTime = flank::defaultTestTime.require(),
    defaultClassTestTime = flank::defaultClassTestTime.require(),
    useAverageTestTimeForNewTests = flank::useAverageTestTimeForNewTests.require(),
    disableUsageStatistics = flank.disableUsageStatistics ?: false,
    outputReportType = OutputReportType.fromName(flank.outputReport),
    skipConfigValidation = flank::skipConfigValidation.require(),
    customShardingJson = flank::customShardingJson.require()
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
        (!flank.disableSharding!! && flank.maxTestShards!! > 1)
