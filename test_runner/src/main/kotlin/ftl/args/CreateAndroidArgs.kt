package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.AndroidConfig
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.util.require

fun createAndroidArgs(
    config: AndroidConfig? = null,
    gcloud: AndroidGcloudConfig = config!!.platform.gcloud,
    flank: AndroidFlankConfig = config!!.platform.flank,
    commonArgs: CommonArgs = config!!.prepareAndroidCommonConfig(),
    obfuscate: Boolean = false
) = AndroidArgs(
    commonArgs = commonArgs,
    // gcloud
    appApk = gcloud.app?.normalizeFilePath(),
    testApk = gcloud.test?.normalizeFilePath(),
    useOrchestrator = gcloud::useOrchestrator.require(),
    testTargets = gcloud::testTargets.require().filterNotNull(),
    testRunnerClass = gcloud.testRunnerClass,
    roboDirectives = gcloud::roboDirectives.require().parseRoboDirectives(),
    performanceMetrics = gcloud::performanceMetrics.require(),
    numUniformShards = gcloud.numUniformShards,
    environmentVariables = gcloud::environmentVariables.require(),
    autoGoogleLogin = gcloud::autoGoogleLogin.require(),
    additionalApks = gcloud::additionalApks.require().map { it.normalizeFilePath() },
    roboScript = gcloud.roboScript?.normalizeFilePath(),

    // flank
    additionalAppTestApks = flank.additionalAppTestApks?.map { (app, test, env) ->
        AppTestPair(
            app = app?.normalizeFilePath(),
            test = test.normalizeFilePath(),
            environmentVariables = env
        )
    } ?: emptyList(),
    useLegacyJUnitResult = flank::useLegacyJUnitResult.require(),
    scenarioLabels = gcloud::scenarioLabels.require(),
    obfuscateDumpShards = obfuscate,
    obbFiles = gcloud::obbfiles.require(),
    obbNames = gcloud::obbnames.require(),
    grantPermissions = gcloud.grantPermissions,
    testTargetsForShard = gcloud.testTargetsForShard.orEmpty()
)
