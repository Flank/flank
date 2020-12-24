package ftl.run.platform.ios

import flank.common.join
import ftl.args.IosArgs
import ftl.gc.GcStorage
import ftl.ios.xctest.xcTestRunFlow
import ftl.run.model.IosTestContext
import ftl.run.model.XcTestContext
import ftl.util.ShardCounter
import ftl.util.asFileReference
import ftl.util.uploadIfNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun IosArgs.createXcTestContexts(): Flow<IosTestContext> {
    val shardCounter = ShardCounter()
    val xcTestGcsPath = uploadIfNeeded(xctestrunZip.asFileReference()).gcs
    val gcsBucket = resultsBucket
    return xcTestRunFlow().map { xcTestRun ->
        val shardName = shardCounter.next()
        val matrixGcsSuffix = join(resultsDir, shardName)
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)

        val xcTestRunNewFileName =
            StringBuilder(xctestrunFile).insert(xctestrunFile.lastIndexOf("."), "_$shardName").toString()

        val xcTestRunFileGcsPath =
            GcStorage.uploadXCTestFile(xcTestRunNewFileName, gcsBucket, matrixGcsSuffix, xcTestRun)

        XcTestContext(
            xcTestGcsPath = xcTestGcsPath,
            xcTestRunFileGcsPath = xcTestRunFileGcsPath,
            xcodeVersion = xcodeVersion.orEmpty(),
            testSpecialEntitlements = testSpecialEntitlements ?: false,
            matrixGcsPath = matrixGcsPath
        )
    }
}
