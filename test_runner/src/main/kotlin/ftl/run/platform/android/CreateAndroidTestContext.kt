package ftl.run.platform.android

import com.linkedin.dex.parser.DexParser
import ftl.args.AndroidArgs
import ftl.args.ArgsHelper
import ftl.config.FtlConstants
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.util.FlankTestMethod
import ftl.util.downloadIfNeeded
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File

suspend fun AndroidArgs.createAndroidTestContexts(): List<AndroidTestContext> = resolveApks().setupShards(this)

private suspend fun List<AndroidTestContext>.setupShards(
    args: AndroidArgs,
    testFilter: TestFilter = TestFilters.fromTestTargets(args.testTargets)
): List<AndroidTestContext> = coroutineScope {
    map { testContext ->
        async {
            if (testContext !is InstrumentationTestContext) testContext
            else testContext.downloadApks().calculateShards(
                args = args,
                testFilter = testFilter
            )
        }
    }.awaitAll().dropEmptyInstrumentationTest()
}

private fun InstrumentationTestContext.downloadApks(): InstrumentationTestContext = copy(
    app = app.downloadIfNeeded(),
    test = test.downloadIfNeeded()
)

private fun InstrumentationTestContext.calculateShards(
    args: AndroidArgs,
    testFilter: TestFilter = TestFilters.fromTestTargets(args.testTargets)
): InstrumentationTestContext = copy(
    shards = ArgsHelper.calculateShards(
        filteredTests = getFlankTestMethods(testFilter),
        args = args,
        forcedShardCount = args.numUniformShards
    ).filter { it.isNotEmpty() }
)

private fun InstrumentationTestContext.getFlankTestMethods(
    testFilter: TestFilter
): List<FlankTestMethod> =
    DexParser.findTestMethods(test.local).asSequence().distinct().filter(testFilter.shouldRun).map { testMethod ->
        FlankTestMethod(
            testName = "class ${testMethod.testName}",
            ignored = testMethod.annotations.any { it.name == "org.junit.Ignore" }
        )
    }.toList()

private fun List<AndroidTestContext>.dropEmptyInstrumentationTest(): List<AndroidTestContext> =
    filterIsInstance<InstrumentationTestContext>().filter { it.shards.isEmpty() }.let { withoutTests ->
        if (withoutTests.isNotEmpty())
            printNoTests(withoutTests)
        minus(withoutTests)
    }

private fun printNoTests(testApks: List<InstrumentationTestContext>) {
    val testApkNames = testApks.joinToString(", ") { pathname -> File(pathname.test.local).name }
    println("${FtlConstants.indent}No tests for $testApkNames")
    println()
}
