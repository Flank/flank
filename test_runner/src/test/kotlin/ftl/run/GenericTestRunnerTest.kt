package ftl.run

import ftl.args.IArgs
import ftl.run.GenericTestRunner.beforeRunMessage
import ftl.test.util.TestHelper.assert
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class GenericTestRunnerTest {

    private fun createMock(testRuns: Int, testShardChunks: List<List<String>>): IArgs {
        val args = mock(IArgs::class.java)
        `when`(args.testRuns).thenReturn(testRuns)
        `when`(args.testShardChunks).thenReturn(testShardChunks)
        return args
    }

    private fun String.trim2(): String {
        return this.split("\n").drop(1).joinToString("\n")
    }

    @Test
    fun testBeforeRunMessage1() {
        val result = beforeRunMessage(createMock(1, listOf(listOf(""))))
        assert(result, "  1 test / 1 shard = 1 test per shard\n")
    }

    @Test
    fun testBeforeRunMessage2() {
        val result = beforeRunMessage(createMock(2, listOf(listOf(""))))
        assert(
            result, """
  1 test / 1 shard = 1 test per shard
  Running 2x
    2 total shards
    2 total tests
""".trim2()
        )
    }

    @Test
    fun testBeforeRunMessage3() {
        val result = beforeRunMessage(
            createMock(
                2,
                listOf(listOf(""), listOf(""), listOf(""), listOf(""), listOf(""), listOf(""))
            )
        )
        assert(
            result, """
  6 tests / 6 shards = 1 test per shard
  Running 2x
    12 total shards
    12 total tests
""".trim2()
        )
    }

    @Test
    fun testBeforeRunMessage4() {
        val result = beforeRunMessage(createMock(100, listOf(listOf("", "", "", "", ""), listOf("", "", "", "", ""))))
        assert(
            result, """
  10 tests / 2 shards = 5 tests per shard
  Running 100x
    200 total shards
    1000 total tests
""".trim2()
        )
    }
}
