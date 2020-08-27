package ftl.run

import ftl.shard.Chunk
import ftl.args.IArgs
import ftl.run.platform.common.beforeRunMessage
import ftl.shard.TestMethod
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.normalizeLineEnding
import ftl.util.trimStartLine
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class GenericTestRunnerTest {

    private fun createMock(repeatTests: Int) = mockk<IArgs>().apply {
        every { this@apply.repeatTests } returns repeatTests
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun testBeforeRunMessage1() {
        val result = beforeRunMessage(createMock(1), listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))).normalizeLineEnding()
        assert(result, "  1 test / 1 shard\n")
    }

    @Test
    fun testBeforeRunMessage2() {
        val result = beforeRunMessage(createMock(2), listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))).normalizeLineEnding()
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
            createMock(2),
            List(6) { Chunk(listOf(TestMethod(name = "", time = 0.0))) }
        ).normalizeLineEnding()
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
        val result = beforeRunMessage(
            createMock(100),
            List(2) { Chunk(listOf(TestMethod(name = "", time = 0.0))) }
        ).normalizeLineEnding()
        assert(
            result, """
  10 tests / 2 shards
  Running 100x
    200 total shards
    1000 total tests
""".trimStartLine()
        )
    }

    @Test
    fun `should print tests + class per shard`() {
        fail("not implemented")
    }

    @Test
    fun `should print class per shard`() {
        fail("not implemented")
    }

    @Test
    fun `should print tests + class per shard with miltiple run`() {
        fail("not implemented")
    }
}
