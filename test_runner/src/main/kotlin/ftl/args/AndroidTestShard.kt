package ftl.args

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import ftl.config.FtlConstants
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.gc.GcStorage
import ftl.util.FlankTestMethod
import java.io.File

object AndroidTestShard {
    private fun getTestApks(args: AndroidArgs, origTestApks: List<String>): List<String> {
        val testApks = if (origTestApks.isNotEmpty()) {
            origTestApks
        } else {
            val allTestApks = mutableListOf<String>()
            val mainTestApk = args.testApk
            if (mainTestApk != null) allTestApks.add(mainTestApk)
            allTestApks.addAll(args.additionalAppTestApks.map { it.test })
            allTestApks
        }

        return testApks.map { testApk ->
            if (testApk.startsWith(FtlConstants.GCS_PREFIX)) GcStorage.download(testApk) else
                testApk
        }
    }

    fun getTestShardChunks(args: AndroidArgs, testApks: List<String> = listOf()): ShardChunks {
        val resolvedApks = getTestApks(args, testApks)
        val filteredTests = resolvedApks.map { getTestMethods(args, it) }.flatten()

        if (filteredTests.isEmpty()) {
            println("${FtlConstants.indent}No tests for ${testApks.joinToString(", ")}")
            return emptyList()
        }

        return if (args.numUniformShards == null) {
            ArgsHelper.calculateShards(filteredTests, args)
        } else {
            listOf(filteredTests.map(FlankTestMethod::testName))
        }
    }

    private fun getTestMethods(args: AndroidArgs, testLocalApk: String): List<FlankTestMethod> {
        val allTestMethods = DexParser.findTestMethods(testLocalApk)
        if (allTestMethods.isEmpty()) {
            // Avoid unnecessary computation if we already know there aren't tests.
            return emptyList()
        }
        val testFilter = TestFilters.fromTestTargets(args.testTargets)
        return allTestMethods filterWith testFilter
    }

    private infix fun List<TestMethod>.filterWith(filter: TestFilter) = asSequence()
        .distinct()
        .filter(filter.shouldRun)
        .map { FlankTestMethod("class ${it.testName}", it.isIgnored) }
        .toList()
}

private val TestMethod.isIgnored: Boolean
    get() = annotations.map { it.name }.contains("org.junit.Ignore")

private inline val String.apkFileName: String
    get() = File(this).name
