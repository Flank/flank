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
import flank.common.logLn
import ftl.api.FileReference
import ftl.args.AndroidArgs
import ftl.args.ArgsHelper
import ftl.args.CalculateShardsResult
import ftl.config.Device
import ftl.config.FlankDefaults
import ftl.config.FtlConstants
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.run.model.AndroidTestContext
import ftl.run.model.AndroidTestShards
import ftl.run.model.InstrumentationTestContext
import ftl.shard.Chunk
import ftl.shard.createShardsByTestForShards
import ftl.util.FlankTestMethod
import ftl.util.downloadIfNeeded
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import ftl.shard.TestMethod as ShardTestMethod

suspend fun AndroidArgs.createAndroidTestContexts(): List<AndroidTestContext> = resolveApks().setupShards()

private val customTestAnnotations = listOf("org.junit.experimental.theories.Theory")

private suspend fun List<AndroidTestContext>.setupShards(): List<AndroidTestContext> = coroutineScope {
    map { testContext ->
        async {
            val newArgs = testContext.args
            val filters = TestFilters.fromTestTargets(newArgs.testTargets, newArgs.testTargetsForShard)
            when {
                testContext !is InstrumentationTestContext -> testContext
                newArgs.useCustomSharding -> testContext.userShards(newArgs.customSharding)
                newArgs.useTestTargetsForShard -> testContext.downloadApks().calculateDummyShards(newArgs, filters)
                else -> testContext.downloadApks().calculateShards(newArgs, filters)
            }
        }
    }.awaitAll().dropEmptyInstrumentationTest()
}

private fun InstrumentationTestContext.userShards(
    customShardingMap: Map<String, AndroidTestShards>
) = customShardingMap
    .values
    .firstOrNull { app.hasReference(it.app) && test.hasReference(it.test) }
    ?.let { customSharding ->
        copy(
            shards = customSharding.shards
                .map { methods -> Chunk(methods.value.map(::ShardTestMethod)) },
            ignoredTestCases = customSharding.junitIgnored
        )
    }
    ?: this

private fun FileReference.hasReference(path: String) = local == path || remote == path

private val AndroidArgs.useCustomSharding: Boolean
    get() = customSharding.isNotEmpty()

private val AndroidArgs.useTestTargetsForShard: Boolean
    get() = testTargetsForShard.isNotEmpty()

private fun InstrumentationTestContext.downloadApks(): InstrumentationTestContext = copy(
    app = app.downloadIfNeeded(),
    test = test.downloadIfNeeded()
)

private fun InstrumentationTestContext.calculateShardsNormally(
    args: AndroidArgs,
    testFilter: TestFilter = TestFilters.fromTestTargets(args.testTargets, args.testTargetsForShard)
): InstrumentationTestContext = ArgsHelper.calculateShards(
    filteredTests = getFlankTestMethods(
        testFilter = testFilter,
        parameterizedTests = args.parameterizedTests
    ),
    args = args,
    forcedShardCount = args.numUniformShards
).run {
    copy(
        shards = shardChunks.filter { it.testMethods.isNotEmpty() },
        ignoredTestCases = ignoredTestCases
    )
}

private fun InstrumentationTestContext.calculateShards(
    args: AndroidArgs,
    testFilter: TestFilter = TestFilters.fromTestTargets(args.testTargets, args.testTargetsForShard)
): InstrumentationTestContext = if (args.parameterizedTests.shouldShardSmartly()) {
    var flankTestMethods = getFlankTestMethods(testFilter, args.parameterizedTests)
    val parameterizedTests = flankTestMethods.filter { it.isParameterizedClass }
    val sdkSuppressTests = flankTestMethods.filter { it.sdkSuppressLevels != null }
    flankTestMethods = flankTestMethods.filterNot { it.isParameterizedClass }.filterNot { it.sdkSuppressLevels != null }
    val shards = calculateParameterizedShards(flankTestMethods, args)
    val shardCount = if (args.parameterizedTests.isSingleParameterizedShard()) 1 else parameterizedTests.size
    val parameterizedShard = calculateParameterizedShards(parameterizedTests, args, shardCount)
    val sdkSuppressShard = calculateParameterizedShards(sdkSuppressTests, args, shardCount)
    shards.copy(shards = shards.shards + parameterizedShard.shards + sdkSuppressShard.shards)
} else calculateShardsNormally(args, testFilter)

private fun InstrumentationTestContext.calculateParameterizedShards(
    filteredTests: List<FlankTestMethod>,
    args: AndroidArgs,
    shardCount: Int? = args.numUniformShards
): InstrumentationTestContext = ArgsHelper.calculateShards(
    filteredTests = filteredTests,
    args = args,
    forcedShardCount = shardCount
).run {
    copy(
        shards = shardChunks.filter { it.testMethods.isNotEmpty() },
        ignoredTestCases = ignoredTestCases
    )
}

private fun InstrumentationTestContext.calculateDummyShards(
    args: AndroidArgs,
    testFilter: TestFilter = TestFilters.fromTestTargets(args.testTargets, args.testTargetsForShard)
): InstrumentationTestContext {
    val filteredTests = getFlankTestMethods(testFilter, args.parameterizedTests)
    val shardsResult = if (filteredTests.isEmpty()) {
        CalculateShardsResult(emptyList(), emptyList())
    } else {
        CalculateShardsResult(
            shardChunks = ArgsHelper.testMethodsAlwaysRun(args.createShardsByTestForShards(), args),
            ignoredTestCases = emptyList()
        )
    }
    return copy(
        shards = shardsResult.shardChunks.filter { it.testMethods.isNotEmpty() },
        ignoredTestCases = shardsResult.ignoredTestCases
    )
}

@VisibleForTesting
internal fun InstrumentationTestContext.getFlankTestMethods(
    testFilter: TestFilter,
    parameterizedTests: String = FlankDefaults.DEFAULT_PARAMETERIZED_TESTS
): List<FlankTestMethod> =
    getParametrizedClasses().let { parameterizedClasses: List<TestMethod> ->
        DexParser.findTestMethods(test.local, customTestAnnotations)
            .asSequence()
            .distinctBy { it.testName }
            .filter(testFilter.shouldRun)
            .filterNot(parameterizedClasses::belong)
            .map { testMethod -> testMethod.toFlankTestMethod(args.devices) }
            .toList()
            .plus(parameterizedClasses.onlyShouldRun(testFilter, parameterizedTests.shouldIgnore()))
    }

private fun List<TestMethod>.belong(method: TestMethod) = any {
    method.testName.startsWith(it.testName)
}

private fun List<TestMethod>.onlyShouldRun(filter: TestFilter, shouldIgnore: Boolean) = if (shouldIgnore) {
    emptyList()
} else this.filter { filter.shouldRun(it) }
    .map { FlankTestMethod("class ${it.testName}", ignored = false, isParameterizedClass = true) }

private fun TestMethod.toFlankTestMethod(devices: List<Device>) = FlankTestMethod(
    testName = "class $testName",
    ignored = annotations.any { it.name in ignoredAnnotations } || !isApplicableToAnyTestDevice(devices),
    sdkSuppressLevels = if (isSdkSuppressTest()) sdkSuppressLevels() else null
)

private fun TestMethod.isApplicableToAnyTestDevice(devices: List<Device>): Boolean {
    val sdkSuppressLevels = sdkSuppressLevels()
    val min = sdkSuppressLevels.minSdkVersion ?: Int.MIN_VALUE
    val max = sdkSuppressLevels.maxSdkVersion ?: Int.MAX_VALUE
    return devices.any { it.version.toInt() in (min..max) }
}

private val ignoredAnnotations = listOf(
    "org.junit.Ignore",
    "androidx.test.filters.Suppress",
    "android.support.test.filters.Suppress"
)

private fun String.shouldShardSmartly() = (this == "shard-into-single" || this == "shard-into-multiple")
private fun String.isSingleParameterizedShard() = (this == "shard-into-single")
private fun String.shouldIgnore(): Boolean = (this == "ignore-all")

@VisibleForTesting
internal fun InstrumentationTestContext.getParametrizedClasses(): List<TestMethod> =
    DexParser.readDexFiles(test.local).fold(emptyList()) { accumulator, file: DexFile ->
        accumulator + file.classDefs
            .filter(file::isParametrizedClass)
            .map {
                TestMethod(
                    testName = file.formatClassName(it).dropLast(1),
                    annotations = file.getClassAnnotationValues(file.getAnnotationsDirectory(it))
                )
            }
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
    logLn("${FtlConstants.indent}No tests for $testApkNames")
    logLn()
}
