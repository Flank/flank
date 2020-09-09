package ftl.shard

import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.util.FlankTestMethod
import io.mockk.every
import io.mockk.mockk

internal fun sample(): JUnitTestResult {

    val testCases = mutableListOf(
        JUnitTestCase("a", "a", "1.0"),
        JUnitTestCase("b", "b", "2.0"),
        JUnitTestCase("c", "c", "4.0"),
        JUnitTestCase("d", "d", "6.0"),
        JUnitTestCase("e", "e", "0.5"),
        JUnitTestCase("f", "f", "2.0"),
        JUnitTestCase("g", "g", "1.0")
    )

    val suite1 = JUnitTestSuite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", testCases, null, null, null)
    val suite2 = JUnitTestSuite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", mutableListOf(), null, null, null)

    return JUnitTestResult(mutableListOf(suite1, suite2))
}

internal fun listOfFlankTestMethod(vararg args: String) = listOf(*args).map { FlankTestMethod(it) }

internal fun mockArgs(maxTestShards: Int, shardTime: Int = 0): IArgs {
    val mockArgs = mockk<IosArgs>()
    every { mockArgs.maxTestShards } returns maxTestShards
    every { mockArgs.shardTime } returns shardTime
    every { mockArgs.defaultTestTime } returns DEFAULT_TEST_TIME_SEC
    every { mockArgs.defaultClassTestTime } returns DEFAULT_CLASS_TEST_TIME_SEC
    every { mockArgs.useAverageTestTimeForNewTests } returns false
    return mockArgs
}
