import com.linkedin.dex.parser.DexParser

import java.util.stream.Collectors

import GlobalConfig.appApk
import GlobalConfig.testApk
import GcStorage.uploadApk
import TestRunner.pollTests
import TestRunner.scheduleApks

object Main {

    fun runOneTestPerVM() {
        println("Test runner started.")
        val stopWatch = StopWatch().start()

        val appApk = appApk
        val testApk = testApk

        val appApkGcsPath = uploadApk(appApk)
        val testApkGcsPath = uploadApk(testApk)
        var testMethodNames = DexParser.findTestNames(testApk.toString())
        testMethodNames = testMethodNames.stream().map { i -> "class " + i }.collect(Collectors.toList())

        println("Running " + testMethodNames.size + " tests")
        val testMatrixIds = TestRunner.scheduleTests(appApkGcsPath, testApkGcsPath, testMethodNames, RunConfig())

        val allTestsSuccessful = pollTests(testMatrixIds)

        println("Finished in " + stopWatch.end())

        val exitCode = if (allTestsSuccessful) 0 else -1
        System.exit(exitCode)
    }

    fun runAllTestsInOneVM() {
        val runConfig = RunConfig(testTimeout = "50m")

        println("Test runner started.")
        val stopWatch = StopWatch().start()

        val testMatrixIds = scheduleApks(appApk, testApk, shardCount = 1, runConfig = runConfig)

        val allTestsSuccessful = pollTests(testMatrixIds)

        println("Finished in " + stopWatch.end())

        val exitCode = if (allTestsSuccessful) 0 else -1
        System.exit(exitCode)
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        runAllTestsInOneVM()
    }
}
