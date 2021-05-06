package ftl.domain.junit

import ftl.api.JUnitTest

fun JUnitTest.Case.empty(): Boolean {
    return name == null || classname == null || time == null
}

/** Failed means there was a failure or an error. */
fun JUnitTest.Case.failed(): Boolean {
    return failures?.isNotEmpty() == true || errors?.isNotEmpty() == true
}

fun JUnitTest.Case.skipped(): Boolean {
    return skipped == null
}

fun JUnitTest.Case.successful(): Boolean {
    return failed().not().and(skipped().not())
}

fun JUnitTest.Result.successful(): Boolean {
    var successful = true
    testsuites?.forEach { suite ->
        if (suite.failed()) successful = false
    }

    return successful
}

fun JUnitTest.Suite.successful(): Boolean {
    return failures == "0" && errors == "0"
}

fun JUnitTest.Suite.failed(): Boolean {
    return successful().not()
}
