package ftl.run.platform.ios

import flank.common.join
import ftl.adapter.google.asFileReference
import ftl.args.IosArgs
import ftl.run.model.GameloopTestContext
import ftl.run.model.IosTestContext
import ftl.util.ShardCounter
import ftl.util.uploadIfNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal fun IosArgs.createGameloopTestContexts(): Flow<IosTestContext> {
    val shardCounter = ShardCounter()
    val appGcsPath = uploadIfNeeded(app.asFileReference()).gcs
    val gcsBucket = resultsBucket
    val shardName = shardCounter.next()
    val matrixGcsSuffix = join(resultsDir, shardName)
    val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)
    return flowOf(GameloopTestContext(appGcsPath, scenarioNumbers.map { it.toInt() }, matrixGcsPath))
}
