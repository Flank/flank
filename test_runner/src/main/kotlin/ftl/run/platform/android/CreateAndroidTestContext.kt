package ftl.run.platform.android

import com.google.common.annotations.VisibleForTesting
import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod
import com.linkedin.dex.parser.formatClassName
import com.linkedin.dex.parser.getAnnotationsDirectory
import com.linkedin.dex.parser.getClassAnnotationValues
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
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
): InstrumentationTestContext = ArgsHelper.calculateShards(
    filteredTests = getFlankTestMethods(testFilter),
    args = args,
    forcedShardCount = args.numUniformShards
).run {
    copy(
        shards = shardChunks.filter { it.testMethods.isNotEmpty() },
        ignoredTestCases = ignoredTestCases
    )
}

@VisibleForTesting
internal fun InstrumentationTestContext.getFlankTestMethods(
    testFilter: TestFilter
): List<FlankTestMethod> =
    getParametrizedClasses().let { parameterizedClasses: List<String> ->
        DexParser.findTestMethods(test.local).asSequence()
            .distinctBy { it.testName }
            .filter(testFilter.shouldRun)
            .filterNot(parameterizedClasses::belong)
            .map(TestMethod::toFlankTestMethod).toList()
            .plus(parameterizedClasses.map(String::toFlankTestMethod))
    }

private fun List<String>.belong(method: TestMethod) = any { className -> method.testName.startsWith(className) }

private fun TestMethod.toFlankTestMethod() = FlankTestMethod(
    testName = "class $testName",
    ignored = annotations.any { it.name in ignoredAnnotations }
)

private val ignoredAnnotations = listOf(
    "org.junit.Ignore",
    "androidx.test.filters.Suppress",
    "android.support.test.filters.Suppress"
)

private fun String.toFlankTestMethod() = FlankTestMethod("class $this", ignored = false, isParameterizedClass = true)

private fun InstrumentationTestContext.getParametrizedClasses(): List<String> =
    DexParser.readDexFiles(test.local).fold(emptyList()) { accumulator, file: DexFile ->
        accumulator + file.classDefs
            .filter(file::isParametrizedClass)
            .map(file::formatClassName) // returns class name + '#'
            .map { it.dropLast(1) } // so drop '#'
    }

private fun DexFile.isParametrizedClass(classDef: ClassDefItem): Boolean =
    getClassAnnotationValues(getAnnotationsDirectory(classDef)).let { annotations: List<TestAnnotation> ->
        annotations.any { it.name.contains("RunWith", ignoreCase = true) } && annotations
            .flatMap { it.values.values }
            .filterIsInstance<DecodedValue.DecodedType>()
            .any { it.isParameterizedAnnotation() }
    }

private fun DecodedValue.DecodedType.isParameterizedAnnotation(): Boolean =
    parameterizedTestRunners.any { runner ->
        value.contains(runner, ignoreCase = true)
    }

private val parameterizedTestRunners = listOf("JUnitParamsRunner", "Parameterized")

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
