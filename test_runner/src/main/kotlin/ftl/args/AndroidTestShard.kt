package ftl.args

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import ftl.config.FtlConstants
import ftl.filter.TestFilters
import ftl.gc.GcStorage
import ftl.util.Utils
import kotlinx.coroutines.runBlocking

object AndroidTestShard {

    // computed properties not specified in yaml
    fun getTestShardChunks(args: AndroidArgs, testApk: String): List<List<String>> {
        if (args.disableSharding) return listOf(emptyList())

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

    private fun getTestMethods(args: AndroidArgs, testLocalApk: String): List<String> {
        val allTestMethods = DexParser.findTestMethods(testLocalApk)
        require(allTestMethods.isNotEmpty()) { Utils.fatalError("Test APK has no tests") }
        val testFilter = TestFilters.fromTestTargets(args.testTargets)
        val filteredTests = allTestMethods
            .asSequence()
            .distinct()
            .filter(testFilter.shouldRun)
            .map(TestMethod::testName)
            .map { "class $it" }
            .toList()
        require(FtlConstants.useMock || filteredTests.isNotEmpty()) { Utils.fatalError("All tests filtered out") }
        return filteredTests
    }
}
