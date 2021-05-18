package ftl.run.platform

import flank.common.join
import flank.common.logLn
import ftl.api.RemoteStorage
import ftl.api.executeTestMatrixAndroid
import ftl.api.uploadToRemoteStorage
import ftl.args.AndroidArgs
import ftl.args.isInstrumentationTest
import ftl.args.shardsFilePath
import ftl.config.FtlConstants
import ftl.run.exception.FlankGeneralError
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.TestResult
import ftl.run.platform.android.asMatrixTestShards
import ftl.run.platform.android.createAndroidTestConfig
import ftl.run.platform.android.createAndroidTestContexts
import ftl.run.platform.android.createAndroidTestMatrixType
import ftl.run.platform.android.upload
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
        }
        .map { context -> createAndroidTestMatrixType(context) }
        .run { executeTestMatrixAndroid(createAndroidTestConfig(args), toList()) }
        .takeIf { it.isNotEmpty() }
        ?: throw FlankGeneralError("There are no tests to run.")

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
