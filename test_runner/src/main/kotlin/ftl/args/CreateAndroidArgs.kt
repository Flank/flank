package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.AndroidConfig
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.run.status.OutputStyle

fun createAndroidArgs(
    config: AndroidConfig? = null,
    gcloud: AndroidGcloudConfig = config!!.platform.gcloud,
    flank: AndroidFlankConfig = config!!.platform.flank,
    commonArgs: CommonArgs = config!!.common.createCommonArgs(config.data)
) = AndroidArgs(
    commonArgs = commonArgs.copy(
        outputStyle = if (flank.hasAdditionalTestAppApk)
            OutputStyle.Single else
            commonArgs.outputStyle
    ),

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

private val AndroidFlankConfig.hasAdditionalTestAppApk get() = additionalAppTestApks?.size ?: 0 > 0
