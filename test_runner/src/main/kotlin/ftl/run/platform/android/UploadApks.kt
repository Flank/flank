package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.GameLoopContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.model.SanityRoboTestContext
import ftl.util.asFileReference
import ftl.util.uploadIfNeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Upload an APK pair if the path given is local
 *
 * @return AppTestPair with their GCS paths
 */
suspend fun List<AndroidTestContext>.upload(rootGcsBucket: String, runGcsPath: String) = coroutineScope {
    map { context -> async(Dispatchers.IO) { context.upload(rootGcsBucket, runGcsPath) } }.awaitAll()
}

private fun AndroidTestContext.upload(rootGcsBucket: String, runGcsPath: String) = when (this) {
    is InstrumentationTestContext -> upload(rootGcsBucket, runGcsPath)
    is RoboTestContext -> upload(rootGcsBucket, runGcsPath)
    is SanityRoboTestContext -> upload(rootGcsBucket, runGcsPath)
    is GameLoopContext -> upload(rootGcsBucket, runGcsPath)
}

private fun InstrumentationTestContext.upload(rootGcsBucket: String, runGcsPath: String) = copy(
    app = app.uploadIfNeeded(rootGcsBucket, runGcsPath),
    test = test.uploadIfNeeded(rootGcsBucket, runGcsPath)
)

private fun RoboTestContext.upload(rootGcsBucket: String, runGcsPath: String) = copy(
    app = app.uploadIfNeeded(rootGcsBucket, runGcsPath),
    roboScript = roboScript.uploadIfNeeded(rootGcsBucket, runGcsPath)
)

private fun GameLoopContext.upload(rootGcsBucket: String, runGcsPath: String) = copy(
    app = app.uploadIfNeeded(rootGcsBucket, runGcsPath),
)

private fun SanityRoboTestContext.upload(rootGcsBucket: String, runGcsPath: String) =
    SanityRoboTestContext(app.uploadIfNeeded(rootGcsBucket, runGcsPath))

suspend fun AndroidArgs.uploadAdditionalApks() =
    additionalApks.uploadToGcloudIfNeeded(resultsDir, resultsBucket)

suspend fun IosArgs.uploadAdditionalIpas() =
    additionalIpas.uploadToGcloudIfNeeded(resultsDir, resultsBucket)

private suspend fun List<String>.uploadToGcloudIfNeeded(
    runGcsPath: String,
    resultsBucket: String
) = coroutineScope {
    map {
        async { it.asFileReference().uploadIfNeeded(resultsBucket, runGcsPath).remote }
    }.awaitAll()
}
