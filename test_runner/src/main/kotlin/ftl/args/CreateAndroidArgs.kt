package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.AndroidConfig
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig

fun createAndroidArgs(
    config: AndroidConfig? = null,
    gcloud: AndroidGcloudConfig = config!!.platform.gcloud,
    flank: AndroidFlankConfig = config!!.platform.flank,
    commonArgs: CommonArgs = config!!.prepareAndroidCommonConfig()
) = AndroidArgs(
    commonArgs = commonArgs,
    // gcloud
    appApk = gcloud.app?.normalizeFilePath("from app"),
    testApk = gcloud.test?.normalizeFilePath("from test"),
    useOrchestrator = gcloud.useOrchestrator!!,
    testTargets = gcloud.testTargets!!.filterNotNull(),
    testRunnerClass = gcloud.testRunnerClass,
    roboDirectives = gcloud.roboDirectives!!.parseRoboDirectives(),
    performanceMetrics = gcloud.performanceMetrics!!,
    otherFiles = gcloud.otherFiles!!.mapValues { (_, path) -> path.normalizeFilePath("from otherFiles") },
    numUniformShards = gcloud.numUniformShards,
    environmentVariables = gcloud.environmentVariables!!,
    directoriesToPull = gcloud.directoriesToPull!!,
    autoGoogleLogin = gcloud.autoGoogleLogin!!,
    additionalApks = gcloud.additionalApks!!.map { it.normalizeFilePath("from additional-apks") },
    roboScript = gcloud.roboScript?.normalizeFilePath("from roboScript"),

    // flank
    additionalAppTestApks = flank.additionalAppTestApks?.map { (app, test) ->
        AppTestPair(
            app = app?.normalizeFilePath("from additional-app-test-apks.app"),
            test = test.normalizeFilePath("from additional-app-test-apks.test")
        )
    } ?: emptyList(),
    useLegacyJUnitResult = flank.useLegacyJUnitResult!!
)
