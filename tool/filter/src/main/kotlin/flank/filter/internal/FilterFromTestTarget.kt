package flank.filter.internal

import flank.filter.Target
import flank.filter.internal.factory.allOf
import flank.filter.internal.factory.anyOf
import flank.filter.internal.factory.fromTestFile
import flank.filter.internal.factory.not
import flank.filter.internal.factory.withAnnotation
import flank.filter.internal.factory.withClassName
import flank.filter.internal.factory.withPackageName
import flank.filter.internal.factory.withSize

internal fun fromTestTargets(testTargets: List<String>): Test.Filter {
    val parsedFilters: List<Test.Filter> = testTargets
        .asSequence()
        .map(String::trim)
        .map { parseTarget(it).createFilter() }
        .toList()

    // select test method name filters and short circuit if they match ex: class a.b#c
    val annotationFilters = parsedFilters.filter { it.isAnnotation }.toTypedArray()
    val otherFilters = parsedFilters.filterNot { it.isAnnotation }
    val exclude = otherFilters.filter { it.describe.startsWith("not") }.toTypedArray()
    val include = otherFilters.filterNot { it.describe.startsWith("not") }.toTypedArray()

    return allOf(*annotationFilters, *exclude, anyOf(*include))
}

private fun parseTarget(target: String): Target =
    target.split(" ", limit = 2).run {
        require(size == 2) { "Invalid argument: $target" }
        first() to last().split(",").map(String::trim).apply {
            require(isNotEmpty()) { "Empty args parsed from $target" }
        }
    }

private fun Target.createFilter(): Test.Filter =
    when (type) {
        Test.Target.Type.ARGUMENT_TEST_CLASS -> withClassName(args)
        Test.Target.Type.ARGUMENT_NOT_TEST_CLASS -> not(withClassName(args))
        Test.Target.Type.ARGUMENT_TEST_PACKAGE -> withPackageName(args)
        Test.Target.Type.ARGUMENT_NOT_TEST_PACKAGE -> not(withPackageName(args))
        Test.Target.Type.ARGUMENT_ANNOTATION -> withAnnotation(args)
        Test.Target.Type.ARGUMENT_NOT_ANNOTATION -> not(withAnnotation(args))
        Test.Target.Type.ARGUMENT_TEST_FILE -> fromTestFile(args)
        Test.Target.Type.ARGUMENT_NOT_TEST_FILE -> not(fromTestFile(args))
        Test.Target.Type.ARGUMENT_TEST_SIZE -> withSize(args)
        else -> throw IllegalArgumentException("Filtering option $type not supported")
    }

private val Target.type get() = first
private val Target.args get() = second
