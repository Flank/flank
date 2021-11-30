package ftl.run.platform

import flank.common.join
import flank.common.logLn
import flank.tool.analytics.mixpanel.Mixpanel
import ftl.api.RemoteStorage
import ftl.api.TestMatrixAndroid
import ftl.api.executeTestMatrixAndroid
import ftl.api.uploadToRemoteStorage
import ftl.args.AndroidArgs
import ftl.args.isInstrumentationTest
import ftl.args.shardsFilePath
import ftl.client.google.getAndroidAppDetails
import ftl.config.FtlConstants
import ftl.run.exception.FlankGeneralError
import ftl.run.model.*
import ftl.run.platform.android.*
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.run.saveShardChunks
import ftl.shard.Chunk
import ftl.shard.testCases
import ftl.util.saveToFlankLinks
import kotlinx.coroutines.coroutineScope
import java.nio.file.Files
import java.nio.file.Paths

internal suspend fun AndroidArgs.runAndroidTests(): TestResult = coroutineScope {
    val args = this@runAndroidTests
    val stopwatch = beforeRunTests()
    val allTestShardChunks = mutableListOf<Chunk>()
    val ignoredTestsShardChunks = mutableListOf<List<String>>()

    val testMatricesData = createAndroidTestContexts()
        .dumpShards(args)
        .upload(resultsBucket, resultsDir)
        .onEach { context ->
            if (context is InstrumentationTestContext) {
                ignoredTestsShardChunks += context.ignoredTestCases
                allTestShardChunks += context.shards
            }

            if (args.appApk?.endsWith(".aab") != true) {
                context.reportPackageName()
            }
        }
        .map { createTestSetup(it) }
        .run { executeTestMatrixAndroid(this) }
        .takeIf { it.isNotEmpty() }
        ?: throw FlankGeneralError("There are no Android tests to run.")

    logLn(beforeRunMessage(allTestShardChunks))

    TestResult(
        matrixMap = afterRunTests(testMatricesData, stopwatch),
        shardChunks = allTestShardChunks.testCases,
        ignoredTests = ignoredTestsShardChunks.flatten()
    )
}

private fun List<AndroidTestContext>.dumpShards(
    config: AndroidArgs
) = takeIf { config.isInstrumentationTest }?.apply {
    saveToFlankLinks(
        config.shardsFilePath,
        FtlConstants.GCS_STORAGE_LINK + join(config.resultsBucket, config.resultsDir)
    )
    if (config.testTargetsForShard.isEmpty())
        filterIsInstance<InstrumentationTestContext>()
            .asMatrixTestShards()
            .saveShards(config)
    if (config.disableResultsUpload.not())
        uploadToRemoteStorage(
            RemoteStorage.Dir(config.resultsBucket, config.resultsDir),
            RemoteStorage.Data(config.shardsFilePath, Files.readAllBytes(Paths.get(config.shardsFilePath)))
        )
} ?: this

private fun AndroidMatrixTestShards.saveShards(config: AndroidArgs) = saveShardChunks(
    shardFilePath = config.shardsFilePath,
    shards = this,
    size = size,
    obfuscatedOutput = config.obfuscateDumpShards
)

private suspend fun createTestSetup(context: AndroidTestContext) = TestMatrixAndroid.TestSetup(
    config = createAndroidTestConfig(context.args),
    type = context.args.createAndroidTestMatrixType(context)
)

private fun AndroidTestContext.reportPackageName() = when (this) {
    is InstrumentationTestContext -> getAndroidAppDetails(app.remote)
    is RoboTestContext -> getAndroidAppDetails(app.remote)
    is SanityRoboTestContext -> getAndroidAppDetails(app.remote)
    is GameLoopContext -> getAndroidAppDetails(app.remote)
}.sendPackageName()

private fun String.sendPackageName() = Mixpanel.add(Mixpanel.APP_ID, this)
