package ftl.filter

import com.linkedin.dex.parser.TestMethod
import flank.common.logLn
import ftl.api.ShardChunks
import ftl.config.FtlConstants
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

/**
 * TestFilter similar to https://junit.org/junit4/javadoc/4.12/org/junit/runner/manipulation/Filter.html
 *
 * Annotations are tracked as all annotation filters must match on a test.
 *
 * @property describe - description of the filter
 * @property shouldRun - lambda that returns if a TestMethod should be included in the test run
 * **/
data class TestFilter(
    val describe: String,
    val shouldRun: ((testMethod: TestMethod) -> Boolean),
    val isAnnotation: Boolean = false
)

/**
 * Supports arguments defined in androidx.test.internal.runner.RunnerArgs
 *
 * Multiple annotation arguments will result in the intersection.
 * https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 * **/
object TestFilters {
    private const val ARGUMENT_TEST_CLASS = "class"
    private const val ARGUMENT_NOT_TEST_CLASS = "notClass"

    private const val ARGUMENT_TEST_SIZE = "size"

    private const val ARGUMENT_ANNOTATION = "annotation"
    private const val ARGUMENT_NOT_ANNOTATION = "notAnnotation"

    private const val ARGUMENT_TEST_PACKAGE = "package"
    private const val ARGUMENT_NOT_TEST_PACKAGE = "notPackage"

    private const val ARGUMENT_TEST_FILE = "testFile"
    private const val ARGUMENT_NOT_TEST_FILE = "notTestFile"

    private val FILTER_ARGUMENT by lazy {

        val pattern = listOf(
            ARGUMENT_TEST_CLASS,
            ARGUMENT_NOT_TEST_CLASS,
            ARGUMENT_TEST_SIZE,
            ARGUMENT_ANNOTATION,
            ARGUMENT_NOT_ANNOTATION,
            ARGUMENT_TEST_PACKAGE,
            ARGUMENT_NOT_TEST_PACKAGE,
            ARGUMENT_TEST_FILE,
            ARGUMENT_NOT_TEST_FILE
        ).joinToString("|")

        Pattern.compile("""($pattern)\s+(.+)""")
    }

    private enum class Size(val annotationName: String) {
        LARGE("LargeTest"),
        MEDIUM("MediumTest"),
        SMALL("SmallTest");

        companion object {
            fun from(name: String): Size =
                values().find { it.name.equals(name, true) } ?: throw FlankConfigurationError("Unknown size $name")
        }
    }

    fun fromTestTargets(testTargets: List<String>, testTargetsForShard: ShardChunks = emptyList()): TestFilter {
        val targets = testTargets + testTargetsForShard.flatten()

        val parsedFilters =
            targets
                .asSequence()
                .map(String::trim)
                .map(TestFilters::parseSingleFilter)
                .toList()

        // select test method name filters and short circuit if they match ex: class a.b#c
        val annotationFilters = parsedFilters.filter { it.isAnnotation }.toTypedArray()
        val otherFilters = parsedFilters.filterNot { it.isAnnotation }
        val exclude = otherFilters.filter { it.describe.startsWith("not") }.toTypedArray()
        val include = otherFilters.filterNot { it.describe.startsWith("not") }.toTypedArray()

        val result = allOf(*annotationFilters, *exclude, anyOf(*include))
        if (FtlConstants.useMock) logLn(result.describe)
        return result
    }

    private fun parseSingleFilter(target: String): TestFilter {
        val matcher = FILTER_ARGUMENT.matcher(target)
        require(matcher.matches()) { "Invalid argument: $target" }
        val args = matcher.group(2)
            .split(",")
            .map(String::trim)
        val command = matcher.group(1)
        require(args.isNotEmpty()) { "Empty args parsed from $target" }
        return when (command) {
            ARGUMENT_TEST_CLASS -> withClassName(args)
            ARGUMENT_NOT_TEST_CLASS -> not(withClassName(args))
            ARGUMENT_TEST_PACKAGE -> withPackageName(args)
            ARGUMENT_NOT_TEST_PACKAGE -> not(withPackageName(args))
            ARGUMENT_ANNOTATION -> withAnnotation(args)
            ARGUMENT_NOT_ANNOTATION -> not(withAnnotation(args))
            ARGUMENT_TEST_FILE -> fromTestFile(args)
            ARGUMENT_NOT_TEST_FILE -> not(fromTestFile(args))
            ARGUMENT_TEST_SIZE -> withSize(args)
            else -> throw FlankConfigurationError("Filtering option $command not supported")
        }
    }

    private fun withSize(args: List<String>): TestFilter = args.map { Size.from(it).annotationName }.let { annotationNames ->
        TestFilter(
            describe = "withSize (${annotationNames.joinToString(", ")})",
            shouldRun = { testMethod ->
                // Ensure that all annotation with a name matching this size are detected, like
                // https://developer.android.com/reference/android/support/test/filters/LargeTest or
                // https://developer.android.com/reference/androidx/test/filters/LargeTest
                testMethod.annotationNames.any { it.split(".").last() in annotationNames }
            },
            isAnnotation = true
        )
    }

    private fun fromTestFile(args: List<String>): TestFilter {
        require(args.size == 1) { "Invalid file path" }
        val path = Paths.get(args[0])
        try {
            val lines = Files.readAllLines(path)
            // this is really an implementation detail:
            // being the package name most generic one, it is able to filter properly if you pass the package name,
            // the fully qualified class name or the fully qualified method name.
            return withPackageName(lines)
        } catch (e: IOException) {
            throw FlankGeneralError("Unable to read testFile", e)
        }
    }

    private fun withPackageName(packageNames: List<String>): TestFilter = TestFilter(
        describe = "withPackageName (${packageNames.joinToString(", ")})",
        shouldRun = { testMethod ->
            packageNames.any { packageName ->
                testMethod.testName.startsWith(packageName)
            }
        }
    )

    private fun withClassName(classNames: List<String>): TestFilter {
        // splits foo.bar.TestClass1#testMethod1 into [foo.bar.TestClass1, testMethod1]
        fun String.extractClassAndTestNames() = split("#")
        val classFilters = classNames.map { it.extractClassAndTestNames() }
        return TestFilter(
            describe = "withClassName (${classNames.joinToString(", ")})",
            shouldRun = { testMethod -> testMethod.testName.extractClassAndTestNames().matchFilters(classFilters) }
        )
    }

    private fun List<String>.matchFilters(classFilters: List<List<String>>): Boolean {
        fun List<String>.className() = first()
        fun List<String>.methodName() = last()
        return classFilters.any { filter ->
            // When filter.size == 1 all test methods from the class should run therefore we do not compare method names
            // When filter.size != 1 only particular test from the class should be launched and we need to compare method names as well
            className() == filter.className() && (filter.size == 1 || methodName() == filter.methodName())
        }
    }

    private fun withAnnotation(annotations: List<String>): TestFilter = TestFilter(
        describe = "withAnnotation (${annotations.joinToString(", ")})",
        shouldRun = { testMethod ->
            testMethod.annotationNames.any { annotations.contains(it) }
        },
        isAnnotation = true
    )

    private fun not(filter: TestFilter): TestFilter = TestFilter(
        describe = "not (${filter.describe})",
        shouldRun = { testMethod ->
            filter.shouldRun(testMethod).not()
        },
        isAnnotation = filter.isAnnotation
    )

    private fun anyOf(vararg filters: TestFilter): TestFilter = TestFilter(
        describe = "anyOf ${filters.map { it.describe }}",
        shouldRun = { testMethod ->
            filters.isEmpty() || filters.any { filter -> filter.shouldRun(testMethod) }
        }
    )

    private fun allOf(vararg filters: TestFilter): TestFilter = TestFilter(
        describe = "allOf ${filters.map { it.describe }}",
        shouldRun = { testMethod ->
            if (FtlConstants.useMock) logLn(":: ${testMethod.testName} @${testMethod.annotations.firstOrNull()}")
            filters.isEmpty() || filters.all { filter ->
                filter.shouldRun(testMethod).also { result ->
                    if (FtlConstants.useMock) println("  $result ${filter.describe}")
                }
            }
        }
    )

    private val TestMethod.annotationNames get() = annotations.map { it.name }
}
