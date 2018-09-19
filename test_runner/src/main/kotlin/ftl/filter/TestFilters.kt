package ftl.filter

import com.linkedin.dex.parser.TestMethod
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Locale
import java.util.regex.Pattern

typealias TestFilter = (TestMethod) -> Boolean

object TestFilters {
    private const val ANNOTATION_IGNORE = "Ignore"
    private const val ARGUMENT_NOT = "not"
    private const val ARGUMENT_TEST_CLASS = "class"
    private const val ARGUMENT_NOT_TEST_CLASS = ARGUMENT_NOT + ARGUMENT_TEST_CLASS
    private const val ARGUMENT_TEST_PACKAGE = "package"
    private const val ARGUMENT_NOT_TEST_PACKAGE = ARGUMENT_NOT + ARGUMENT_TEST_PACKAGE
    private const val ARGUMENT_TEST_ANNOTATION = "annotation"
    private const val ARGUMENT_NOT_TEST_ANNOTATION = ARGUMENT_NOT + ARGUMENT_TEST_ANNOTATION
    private const val ARGUMENT_TEST_FILE = "testfile"
    private const val ARGUMENT_NOT_TEST_FILE = ARGUMENT_NOT + ARGUMENT_TEST_FILE
    private const val ARGUMENT_TEST_SIZE = "size"
    private val FILTER_ARGUMENT = Pattern.compile(
        "($ARGUMENT_TEST_CLASS|$ARGUMENT_NOT_TEST_CLASS|$ARGUMENT_TEST_PACKAGE|$ARGUMENT_NOT_TEST_PACKAGE|$ARGUMENT_TEST_ANNOTATION|$ARGUMENT_NOT_TEST_ANNOTATION|$ARGUMENT_TEST_FILE|$ARGUMENT_NOT_TEST_FILE|$ARGUMENT_TEST_SIZE)\\s+(.+)",
        Pattern.CASE_INSENSITIVE
    )
    private const val MISSING_ARGUMENTS_MSG = "Must provide either classes to run, or apks to scan"

    private enum class Size(val annotation: String) {
        LARGE("LargeTest"),
        MEDIUM("MediumTest"),
        SMALL("SmallTest");

        companion object {
            fun from(name: String): Size =
                values().find { it.name.equals(name, true) } ?: throw IllegalArgumentException("Unknown size $name")
        }
    }

    fun fromTestTargets(targets: List<String>): TestFilter = if (targets.isEmpty()) {
        notIgnored()
    } else {
        allOf(targets.asSequence().map(String::trim).map(TestFilters::parseSingleFilter).toList() + notIgnored())
    }

    private fun parseSingleFilter(target: String): TestFilter {
        val matcher = FILTER_ARGUMENT.matcher(target)
        require(matcher.matches()) { "Invalid argument: $target" }
        val args = matcher.group(2)
            .split(",")
            .map(String::trim)
        val command = matcher.group(1).toLowerCase(Locale.ENGLISH)
        return when (command) {
            ARGUMENT_TEST_CLASS -> withClassName(args)
            ARGUMENT_NOT_TEST_CLASS -> not(withClassName(args))
            ARGUMENT_TEST_PACKAGE -> withPackageName(args)
            ARGUMENT_NOT_TEST_PACKAGE -> not(withPackageName(args))
            ARGUMENT_TEST_ANNOTATION -> withAnnotation(args)
            ARGUMENT_NOT_TEST_ANNOTATION -> not(withAnnotation(args))
            ARGUMENT_TEST_FILE -> fromTestFile(args)
            ARGUMENT_NOT_TEST_FILE -> not(fromTestFile(args))
            ARGUMENT_TEST_SIZE -> withSize(args)
            else -> throw IllegalArgumentException("Filtering option $command not supported")
        }
    }

    private fun withSize(args: List<String>): TestFilter =
        withAnnotation(args.map { Size.from(it).annotation })

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
            throw RuntimeException("Unable to read testFile", e)
        }
    }

    private fun withPackageName(packageNames: List<String>): TestFilter {
        require(packageNames.isNotEmpty()) { MISSING_ARGUMENTS_MSG }
        return { method ->
            packageNames.any { packageName -> method.testName.startsWith(packageName) }
        }
    }

    private fun withClassName(classNames: List<String>): TestFilter =
        withPackageName(classNames)

    private fun withAnnotation(annotations: List<String>): TestFilter = { method ->
        method.annotationNames.any { annotations.contains(it) }
    }

    private fun notIgnored(): TestFilter =
        not(withAnnotation(listOf(ANNOTATION_IGNORE)))

    private fun not(filter: TestFilter): TestFilter = { method ->
        filter(method).not()
    }

    private fun allOf(filters: List<TestFilter>): TestFilter = { method ->
        filters.all { filter -> filter(method) }
    }

    private val TestMethod.annotationNames get() = annotations.map { it.name }
}
