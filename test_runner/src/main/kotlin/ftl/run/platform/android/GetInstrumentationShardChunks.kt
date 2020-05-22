package ftl.run.platform.android

import com.linkedin.dex.parser.DexParser
import ftl.args.AndroidArgs
import ftl.args.ArgsHelper
import ftl.args.ShardChunks
import ftl.config.FtlConstants
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.run.model.InstrumentationTestApk
import ftl.util.FlankTestMethod
import ftl.util.downloadIfNeeded
import java.io.File

fun getInstrumentationShardChunks(
    args: AndroidArgs,
    testApks: List<InstrumentationTestApk>
): Map<InstrumentationTestApk, ShardChunks> =
    getFlankTestMethods(
        testApks = testApks.download(),
        testFilter = TestFilters.fromTestTargets(args.testTargets)
    ).mapValues { (_, testMethods: List<FlankTestMethod>) ->
        when {
            testMethods.isEmpty() -> emptyList<List<String>>().also { printNoTests(testApks) }
            args.numUniformShards == null -> ArgsHelper.calculateShards(testMethods, args)
            else -> listOf(testMethods.map(FlankTestMethod::testName))
        }
    }

private fun getFlankTestMethods(
    testApks: List<InstrumentationTestApk>,
    testFilter: TestFilter
): Map<InstrumentationTestApk, List<FlankTestMethod>> =
    testApks.associateWith { testApk ->
        DexParser.findTestMethods(testApk.test.local).asSequence().distinct().filter(testFilter.shouldRun).map { testMethod ->
            FlankTestMethod(
                testName = "class ${testMethod.testName}",
                ignored = testMethod.annotations.any { it.name == "org.junit.Ignore" }
            )
        }.toList()
    }

private fun List<InstrumentationTestApk>.download(): List<InstrumentationTestApk> =
    map { reference ->
        reference.copy(
            app = reference.app.downloadIfNeeded(),
            test = reference.test.downloadIfNeeded()
        )
    }

private fun printNoTests(testApks: List<InstrumentationTestApk>) {
    val testApkNames = testApks.joinToString(", ") { pathname -> File(pathname.test.local).name }
    println("${FtlConstants.indent}No tests for $testApkNames")
}
