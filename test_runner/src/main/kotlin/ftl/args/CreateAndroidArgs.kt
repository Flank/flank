package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.AndroidConfig
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.run.common.fromJson
import ftl.run.model.AndroidTestShards
import ftl.util.require
import java.nio.file.Paths

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
    additionalAppTestApks = flank.additionalAppTestApks?.map {
        // if additional-pair did not provide certain values, set as top level ones
        val mergedClientDetails = mutableMapOf<String, String>().apply {
            // merge additionalAppTestApk's client-details with top-level client-details
            putAll(commonArgs.clientDetails ?: emptyMap())
            putAll(it.clientDetails)
        }

        AppTestPair(
            app = it.app?.normalizeFilePath(),
            test = it.test.normalizeFilePath(),
            environmentVariables = it.environmentVariables,
            maxTestShards = if (it.maxTestShards == -1) commonArgs.maxTestShards else it.maxTestShards,
            clientDetails = mergedClientDetails
        )
    } ?: emptyList(),
    useLegacyJUnitResult = flank::useLegacyJUnitResult.require(),
    scenarioLabels = gcloud::scenarioLabels.require(),
    obfuscateDumpShards = obfuscate,
    obbFiles = gcloud::obbfiles.require(),
    obbNames = gcloud::obbnames.require(),
    grantPermissions = gcloud.grantPermissions,
    testTargetsForShard = gcloud.testTargetsForShard?.normalizeToTestTargets().orEmpty(),
    customSharding = createCustomShards(commonArgs.customShardingJson)
)

private fun createCustomShards(shardingJsonPath: String) =
    if (shardingJsonPath.isBlank()) emptyMap()
    else fromJson<Map<String, AndroidTestShards>>(
        Paths.get(shardingJsonPath).toFile().readText()
    ).normalizeShardPaths()

private fun Map<String, AndroidTestShards>.normalizeShardPaths() =
    mapValues { (_, shards) ->
        shards.copy(
            app = shards.app.normalizeFilePath(),
            test = shards.test.normalizeFilePath()
        )
    }
