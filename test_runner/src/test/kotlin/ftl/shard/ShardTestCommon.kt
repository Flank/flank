package ftl.shard

import ftl.api.JUnitTest
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.util.FlankTestMethod
import io.mockk.every
import io.mockk.mockk

internal fun sample(): JUnitTest.Result {
    val testCases = mutableListOf(
        JUnitTest.Case("a", "a", "1.0"),
        JUnitTest.Case("b", "b", "2.0"),
        JUnitTest.Case("c", "c", "4.0"),
        JUnitTest.Case("d", "d", "6.0"),
        JUnitTest.Case("e", "e", "0.5"),
        JUnitTest.Case("f", "f", "2.0"),
        JUnitTest.Case("g", "g", "1.0")
    )

    val suite1 = JUnitTest.Suite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", testCases, null, null, null)
    val suite2 = JUnitTest.Suite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", mutableListOf(), null, null, null)

    return JUnitTest.Result(mutableListOf(suite1, suite2))
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
