package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.util.StopWatch
import ftl.util.Utils
import kotlinx.coroutines.experimental.Deferred

object GenericTestRunner {
    fun beforeRunTests(): Pair<StopWatch, String> {
        println("RunTests")
        val stopwatch = StopWatch().start()
        TestRunner.assertMockUrl()

        return Pair(stopwatch, Utils.uniqueObjectName())
    }

    suspend fun afterRunTests(
            jobs: ArrayList<Deferred<TestMatrix>>,
            runGcsPath: String,
            stopwatch: StopWatch,
            config: IArgs): MatrixMap {
        val savedMatrices = mutableMapOf<String, SavedMatrix>()

        jobs.forEach {
            val matrix = it.await()
            val matrixId = matrix.testMatrixId
            savedMatrices[matrixId] = SavedMatrix(matrix)
        }

        val matrixMap = MatrixMap(savedMatrices, runGcsPath)
        TestRunner.updateMatrixFile(matrixMap)

        println(FtlConstants.indent + "${savedMatrices.size} matrix ids created in ${stopwatch.check()}")
        val gcsBucket = "https://console.developers.google.com/storage/browser/" +
                config.resultsBucket + "/" + matrixMap.runPath
        println(FtlConstants.indent + gcsBucket)
        println()

        return matrixMap
    }

    private fun s(amount: Int): String {
        return if (amount > 1) {
            "s"
        } else {
            ""
        }
    }

    fun beforeRunMessage(args: IArgs): String {
        val runCount = args.testRuns
        val deviceCount = args.testShardChunks.size
        val testsPerDevice = args.testShardChunks.first().size
        val testsCount = args.testShardChunks.sumBy { it.size }

        val result = StringBuilder()
        result.appendln("  $testsCount test${s(testsCount)} / $deviceCount device${s(deviceCount)} = " +
                "$testsPerDevice test${s(testsPerDevice)} per device")

        if (runCount > 1) {
            result.appendln("  Running ${runCount}x")
            val runDevices = runCount * deviceCount
            val runTests = runCount * testsCount
            result.appendln("    $runDevices total device${s(runDevices)}")
            result.appendln("    $runTests total test${s(runTests)}")
        }

        return result.toString()
    }
}
