package ftl.run.platform.ios

import flank.common.join
import ftl.adapter.google.asFileReference
import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.args.IosArgs
import ftl.client.google.uploadIfNeeded
import ftl.ios.xctest.xcTestRunFlow
import ftl.run.model.IosTestContext
import ftl.run.model.XcTestContext
import ftl.util.ShardCounter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun IosArgs.createXcTestContexts(): Flow<IosTestContext> {
    val shardCounter = ShardCounter()
    val xcTestGcsPath = uploadIfNeeded(xctestrunZip.asFileReference()).remote
    val gcsBucket = resultsBucket
    return xcTestRunFlow().map { xcTestRun ->
        val shardName = shardCounter.next()
        val matrixGcsSuffix = join(resultsDir, shardName)
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)

        val xcTestRunNewFileName =
            StringBuilder(xctestrunFile).insert(xctestrunFile.lastIndexOf("."), "_$shardName").toString()

        val xcTestRunFileGcsPath = uploadToRemoteStorage(
            RemoteStorage.Dir(gcsBucket, matrixGcsSuffix),
            RemoteStorage.Data(xcTestRunNewFileName, xcTestRun)
        )

        XcTestContext(
            xcTestGcsPath = xcTestGcsPath,
            xcTestRunFileGcsPath = xcTestRunFileGcsPath,
            xcodeVersion = xcodeVersion.orEmpty(),
            testSpecialEntitlements = testSpecialEntitlements ?: false,
            matrixGcsPath = matrixGcsPath
        )
    }
}
