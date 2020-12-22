package ftl.run.platform.ios

import ftl.args.IosArgs
import ftl.gc.GcStorage
import ftl.ios.xctest.xcTestRunFlow
import ftl.run.model.IosTestContext
import ftl.run.model.XcTestContext
import ftl.util.ShardCounter
import ftl.util.asFileReference
import ftl.util.join
import ftl.util.uploadIfNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun IosArgs.createXcTestContexts(): Flow<IosTestContext> {
    val shardCounter = ShardCounter()
    val xcTestGcsPath = uploadIfNeeded(xctestrunZip.asFileReference()).gcs
    return xcTestRunFlow().map { xcTestRun ->

        val gcsBucket = resultsBucket
        val shardName = shardCounter.next()
        val matrixGcsSuffix = join(resultsDir, shardName)
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)

        val xctestrunNewFileName =
            StringBuilder(xctestrunFile).insert(xctestrunFile.lastIndexOf("."), "_$shardName").toString()

        val xctestrunFileGcsPath =
            GcStorage.uploadXCTestFile(xctestrunNewFileName, gcsBucket, matrixGcsSuffix, xcTestRun)

        XcTestContext(xcTestGcsPath, xctestrunFileGcsPath, xcodeVersion.orEmpty(), testSpecialEntitlements
            ?: false, matrixGcsPath)
    }
}
