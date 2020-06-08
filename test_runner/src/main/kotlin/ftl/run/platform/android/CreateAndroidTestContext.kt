package ftl.run.platform.android

import com.linkedin.dex.parser.*
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
): InstrumentationTestContext = copy(
    shards = ArgsHelper.calculateShards(
        filteredTests = getFlankTestMethods(testFilter),
        args = args,
        forcedShardCount = args.numUniformShards
    ).filter { it.isNotEmpty() }
)

private fun InstrumentationTestContext.getFlankTestMethods(
    testFilter: TestFilter
): List<FlankTestMethod> {
    val parameterizedClasses = mutableListOf<String>()
    DexParser.readDexFiles(test.local).forEach { file ->
        file.classDefs.forEach { classDef ->
            val directory = file.getAnnotationsDirectory(classDef)
            val className = file.formatClassName(classDef).dropLast(1)
            val classAnnotations = file.getClassAnnotationValues(directory).map { it.name }
            if (classAnnotations.any { it.toLowerCase().contains("RunWith".toLowerCase()) } && // sprawdzamy czy klasa ma adnotacje RunWith
                file.getClassAnnotationValues(directory)
                    .flatMap { annotations -> annotations.values.values }
                    .filterIsInstance<DecodedValue.DecodedType>()
                    .map { it.value }
                    .any { it.toLowerCase().contains("Parameterized".toLowerCase()) }) {  // sprawdzamy czy klasa zadana jako parametr  adnotacji to Parameterized
                parameterizedClasses += className
            }
        }
    }
    return DexParser.findTestMethods(test.local).asSequence()
        .distinct()
        .filter(testFilter.shouldRun)
        .filter { method -> parameterizedClasses.none { method.testName.contains(it) } } // wywal metody z klas parameterized
        .map { testMethod ->
            FlankTestMethod(
                testName = "class ${testMethod.testName}",
                ignored = testMethod.annotations.any { it.name == "org.junit.Ignore" }
            )
        }.toList() + parameterizedClasses.map { FlankTestMethod("class $it", false) }
}

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
