package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.AndroidConfig
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.resolve

fun createAndroidArgs(
    config: AndroidConfig? = null,
    gcloud: AndroidGcloudConfig = config!!.platform.gcloud,
    flank: AndroidFlankConfig = config!!.platform.flank,
    commonArgs: CommonArgs = config!!.common.createCommonArgs(config.data)
) = AndroidArgs(
    commonArgs = commonArgs.copy(maxTestShards = commonArgs.calculateMaxTestShards()),
    // gcloud
    appApk = gcloud.app?.processFilePath("from app"),
    testApk = gcloud.test?.processFilePath("from test"),
    useOrchestrator = gcloud.useOrchestrator!!,
    testTargets = gcloud.testTargets!!.filterNotNull(),
    testRunnerClass = gcloud.testRunnerClass,
    roboDirectives = gcloud.roboDirectives!!.parseRoboDirectives(),
    performanceMetrics = gcloud.performanceMetrics!!,
    otherFiles = gcloud.otherFiles!!.mapValues { (_, path) -> path.processFilePath("from otherFiles") },
    numUniformShards = gcloud.numUniformShards,
    environmentVariables = gcloud.environmentVariables!!,
    directoriesToPull = gcloud.directoriesToPull!!,
    autoGoogleLogin = gcloud.autoGoogleLogin!!,
    additionalApks = gcloud.additionalApks!!.map { it.processFilePath("from additional-apks") },
    roboScript = gcloud.roboScript?.processFilePath("from roboScript"),

    // flank
    additionalAppTestApks = flank.additionalAppTestApks?.map { (app, test) ->
        AppTestPair(
            app = app?.processFilePath("from additional-app-test-apks.app"),
            test = test.processFilePath("from additional-app-test-apks.test")
        )
    } ?: emptyList(),
    useLegacyJUnitResult = flank.useLegacyJUnitResult!!
)

private fun CommonArgs.calculateMaxTestShards(): Int = if (maxTestShards == -1) getMaxShardsByDevice() else maxTestShards

private fun CommonArgs.getMaxShardsByDevice() = if (devices.resolve(project).any { it.isVirtual == false }) IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last
else IArgs.AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last
