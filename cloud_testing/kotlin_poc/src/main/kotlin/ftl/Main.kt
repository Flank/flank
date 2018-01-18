package ftl

import com.linkedin.dex.parser.DexParser
import ftl.GcStorage.uploadApk
import ftl.GlobalConfig.appApk
import ftl.GlobalConfig.testApk
import ftl.GlobalConfig.testErrorApk
import ftl.TestRunner.pollTests
import ftl.TestRunner.scheduleApks
import java.util.stream.Collectors

object Main {

    private fun runOneTestPerVM() {
        println("Test runner started.")
        GlobalConfig.downloadXml = false
        val stopWatch = ftl.StopWatch().start()

        val appApk = appApk
        val testApk = testApk

        val appApkGcsPath = uploadApk(appApk)
        val testApkGcsPath = uploadApk(testApk)
        var testMethodNames = DexParser.findTestNames(testApk.toString())
        testMethodNames = testMethodNames.stream().map { i -> "class " + i }.collect(Collectors.toList())

        println("Running " + testMethodNames.size + " tests")
        val testMatrixIds = ftl.TestRunner.scheduleTests(appApkGcsPath, testApkGcsPath, testMethodNames, ftl.RunConfig())

        val allTestsSuccessful = pollTests(testMatrixIds)

        println("Finished in " + stopWatch.end())

        val exitCode = if (allTestsSuccessful) 0 else -1
        System.exit(exitCode)
    }

    private fun runAllTestsInOneVM() {
        println("runAllTestsInOneVM")
        GlobalConfig.downloadXml = true
        val runConfig = ftl.RunConfig(testTimeoutMinutes = 60)

        println("Test runner started.")
        val stopWatch = ftl.StopWatch().start()

        val testMatrixIds = scheduleApks(appApk, testErrorApk, shardCount = 2, runConfig = runConfig)

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
