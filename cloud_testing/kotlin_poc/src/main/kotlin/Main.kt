import com.linkedin.dex.parser.DexParser

import java.util.stream.Collectors

import Config.appApk
import Config.testApk
import GcStorage.uploadApk
import TestRunner.pollTests
import TestRunner.scheduleTests

object Main {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println("Test runner started.")
        val stopWatch = StopWatch().start()

        val appApk = appApk
        val testApk = testApk

        val appApkGcsPath = uploadApk(appApk)
        val testApkGcsPath = uploadApk(testApk)
        var testMethodNames = DexParser.findTestNames(testApk.toString())
        testMethodNames = testMethodNames.stream().map { i -> "class " + i }.collect(Collectors.toList())

        println("Running " + testMethodNames.size + " tests")
        val testMatrixIds = scheduleTests(appApkGcsPath, testApkGcsPath, testMethodNames)

        val allTestsSuccessful = pollTests(testMatrixIds)

        println("Finished in " + stopWatch.end())

        val exitCode = if (allTestsSuccessful) 0 else -1
        System.exit(exitCode)
    }
}
