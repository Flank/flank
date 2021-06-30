package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun withClassName(classNames: List<String>): Test.Filter {
    // splits foo.bar.TestClass1#testMethod1 into [foo.bar.TestClass1, testMethod1]
    val classFilters = classNames.map { it.extractClassAndTestNames() }

    return Test.Filter(
        describe = "withClassName (${classNames.joinToString(", ")})",
        shouldRun = { (name, _) ->
            name.extractClassAndTestNames().matchFilters(classFilters)
        }
    )
}

private fun String.extractClassAndTestNames() = split("#")

private fun List<String>.matchFilters(classFilters: List<List<String>>): Boolean {
    fun List<String>.className() = first()
    fun List<String>.methodName() = last()
    return classFilters.any { filter ->
        // When filter.size == 1 all test methods from the class should run therefore we do not compare method names
        // When filter.size != 1 only particular test from the class should be launched and we need to compare method names as well
        className() == filter.className() && (filter.size == 1 || methodName() == filter.methodName())
    }
}
