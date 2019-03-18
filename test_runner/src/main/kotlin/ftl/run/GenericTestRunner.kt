package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.args.yml.FlankYmlParams
import ftl.config.FtlConstants
import ftl.config.FtlConstants.useMock
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.util.StopWatch
import ftl.util.Utils
import java.io.File

object GenericTestRunner {
    fun beforeRunTests(args: IArgs): Pair<StopWatch, String> {
        println("RunTests")
        val stopwatch = StopWatch().start()
        TestRunner.assertMockUrl()

        val resultsDir = if (args.localResultDir != FlankYmlParams.defaultLocalResultDir) {
            // Only one result is stored when using --local-result-dir
            // Delete any old results if they exist before storing new ones.
            File(args.localResultDir).deleteRecursively()
            "../${args.localResultDir}"
        } else {
            args.resultsDir ?: Utils.uniqueObjectName()
        }

        // Avoid spamming the results/ dir with temporary files from running the test suite.
        if (useMock) {
            Runtime.getRuntime().addShutdownHook(Thread {
                File(args.localResultDir, resultsDir).deleteRecursively()
            })
        }

        return stopwatch to resultsDir
    }

    fun afterRunTests(
        jobs: List<TestMatrix>,
        runGcsPath: String,
        stopwatch: StopWatch,
        config: IArgs
    ): MatrixMap {
        val savedMatrices = mutableMapOf<String, SavedMatrix>()

        jobs.forEach { matrix ->
            val matrixId = matrix.testMatrixId
            savedMatrices[matrixId] = SavedMatrix(matrix)
        }

        val matrixMap = MatrixMap(savedMatrices, runGcsPath)
        TestRunner.updateMatrixFile(matrixMap, config)

        TestRunner.saveConfigFile(matrixMap.runPath, config)

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
        val runCount = args.repeatTests
        val shardCount = args.testShardChunks.size
        val testsCount = args.testShardChunks.sumBy { it.size }

        val result = StringBuilder()
        result.appendln(
            "  $testsCount test${s(testsCount)} / $shardCount shard${s(shardCount)}"
        )

        if (runCount > 1) {
            result.appendln("  Running ${runCount}x")
            val runDevices = runCount * shardCount
            val runTests = runCount * testsCount
            result.appendln("    $runDevices total shard${s(runDevices)}")
            result.appendln("    $runTests total test${s(runTests)}")
        }

        return result.toString()
    }
}
