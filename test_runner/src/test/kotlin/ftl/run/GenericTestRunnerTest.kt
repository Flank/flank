package ftl.run

import ftl.args.IArgs
import ftl.run.GenericTestRunner.beforeRunMessage
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import ftl.util.Utils.trimStartLine
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GenericTestRunnerTest {

    private fun createMock(repeatTests: Int, testShardChunks: List<List<String>>): IArgs {
        val args = mock(IArgs::class.java)
        `when`(args.repeatTests).thenReturn(repeatTests)
        `when`(args.testShardChunks).thenReturn(testShardChunks)
        return args
    }

    @Test
    fun testBeforeRunMessage1() {
        val result = beforeRunMessage(createMock(1, listOf(listOf(""))))
        assert(result, "  1 test / 1 shard\n")
    }

    @Test
    fun testBeforeRunMessage2() {
        val result = beforeRunMessage(createMock(2, listOf(listOf(""))))
        assert(
            result, """
  1 test / 1 shard
  Running 2x
    2 total shards
    2 total tests
""".trimStartLine()
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
  6 tests / 6 shards
  Running 2x
    12 total shards
    12 total tests
""".trimStartLine()
        )
    }

    @Test
    fun testBeforeRunMessage4() {
        val result = beforeRunMessage(createMock(100, listOf(listOf("", "", "", "", ""), listOf("", "", "", "", ""))))
        assert(
            result, """
  10 tests / 2 shards
  Running 100x
    200 total shards
    1000 total tests
""".trimStartLine()
        )
    }
}
