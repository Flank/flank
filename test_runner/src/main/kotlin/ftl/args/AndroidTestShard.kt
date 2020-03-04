package ftl.args

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import ftl.config.FtlConstants
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.gc.GcStorage
import ftl.util.FlankTestMethod
import ftl.util.Utils
import kotlinx.coroutines.runBlocking

object AndroidTestShard {

    // computed properties not specified in yaml
    fun getTestShardChunks(args: AndroidArgs, testApk: String): List<List<String>> {
        // Download test APK if necessary so it can be used to validate test methods
        var testLocalApk = testApk
        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            runBlocking {
                testLocalApk = GcStorage.download(testApk)
            }
        }

        val filteredTests = getTestMethods(args, testLocalApk)
        return ArgsHelper.calculateShards(filteredTests, args)
    }

    private fun getTestMethods(args: AndroidArgs, testLocalApk: String): List<FlankTestMethod> {
        val allTestMethods = DexParser.findTestMethods(testLocalApk)
        val shouldIgnoreMissingTests = allTestMethods.isEmpty() && args.disableSharding
        val shouldThrowErrorIfMissingTests = allTestMethods.isEmpty() && !args.disableSharding
        when {
            shouldIgnoreMissingTests -> return mutableListOf()
            shouldThrowErrorIfMissingTests -> throw IllegalStateException(Utils.fatalError("Test APK has no tests"))
        }
        val testFilter = TestFilters.fromTestTargets(args.testTargets)
        return allTestMethods filterWith testFilter
    }

    private infix fun List<TestMethod>.filterWith(filter: TestFilter) = asSequence()
        .distinct()
        .filter(filter.shouldRun)
        .map { FlankTestMethod("class ${it.testName}", it.isIgnored) }
        .toList()
        .also {
            require(FtlConstants.useMock || it.isNotEmpty()) { Utils.fatalError("All tests filtered out") }
        }
}

private val TestMethod.isIgnored: Boolean
    get() = annotations.map { it.name }.contains("org.junit.Ignore")
