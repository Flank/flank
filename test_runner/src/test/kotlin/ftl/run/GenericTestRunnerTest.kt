package ftl.run

import flank.common.normalizeLineEnding
import flank.common.trimStartLine
import ftl.args.IArgs
import ftl.run.platform.common.beforeRunMessage
import ftl.shard.Chunk
import ftl.shard.TestMethod
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
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
        val result = createMock(1).beforeRunMessage(
            listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))
        ).normalizeLineEnding()
        assert(result.trim(), "  1 test / 1 shard".trim())
    }

    @Test
    fun testBeforeRunMessage2() {
        val result = createMock(2).beforeRunMessage(
            listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))
        ).normalizeLineEnding()
        assert(
            result,
            """

  1 test / 1 shard
  Running 2x
    2 total shards
    2 total tests
""".trimStartLine()
        )
    }

    @Test
    fun testBeforeRunMessage3() {
        val result = createMock(2).beforeRunMessage(
            List(6) { Chunk(listOf(TestMethod(name = "", time = 0.0))) }
        ).normalizeLineEnding()
        assert(
            result,
            """

  6 tests / 6 shards
  Running 2x
    12 total shards
    12 total tests
""".trimStartLine()
        )
    }

    @Test
    fun testBeforeRunMessage4() {
        val result = createMock(100).beforeRunMessage(
            List(2) { Chunk(List(5) { TestMethod(name = "", time = 0.0) }) }
        ).normalizeLineEnding()
        assert(
            result,
            """

  10 tests / 2 shards
  Running 100x
    200 total shards
    1000 total tests
""".trimStartLine()
        )
    }

    @Test
    fun `should print tests + class per shard`() {
        val expected = """
            10 tests + 3 parameterized classes / 2 shards
        """.trimIndent()

        val result = createMock(1).beforeRunMessage(
            listOf(
                Chunk(
                    listOf(
                        TestMethod(name = "t1", time = 0.0),
                        TestMethod(name = "t2", time = 0.0),
                        TestMethod(name = "t3", time = 0.0),
                        TestMethod(name = "c1", time = 0.0, isParameterized = true),
                        TestMethod(name = "c2", time = 0.0, isParameterized = true),
                    )
                ),
                Chunk(
                    listOf(
                        TestMethod(name = "t4", time = 0.0),
                        TestMethod(name = "t5", time = 0.0),
                        TestMethod(name = "t6", time = 0.0),
                        TestMethod(name = "t7", time = 0.0),
                        TestMethod(name = "t8", time = 0.0),
                        TestMethod(name = "t9", time = 0.0),
                        TestMethod(name = "t10", time = 0.0),
                        TestMethod(name = "c3", time = 0.0, isParameterized = true),
                    )
                )
            )
        ).normalizeLineEnding().trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `should print class per shard`() {
        val expected = """
            3 parameterized classes / 2 shards
        """.trimIndent()

        val result = createMock(1).beforeRunMessage(
            listOf(
                Chunk(
                    listOf(
                        TestMethod(name = "c1", time = 0.0, isParameterized = true),
                        TestMethod(name = "c2", time = 0.0, isParameterized = true),
                    )
                ),
                Chunk(
                    listOf(
                        TestMethod(name = "c3", time = 0.0, isParameterized = true),
                    )
                )
            )
        ).normalizeLineEnding().trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `should print tests + class per shard with multiple run`() {
        val expected = """
            10 tests + 3 parameterized classes / 2 shards
            Running 10x
              20 total shards
              100 total tests
              30 total parameterized classes
        """.trimIndent()

        val result = createMock(10).beforeRunMessage(
            listOf(
                Chunk(
                    listOf(
                        TestMethod(name = "t1", time = 0.0),
                        TestMethod(name = "t2", time = 0.0),
                        TestMethod(name = "t3", time = 0.0),
                        TestMethod(name = "c1", time = 0.0, isParameterized = true),
                        TestMethod(name = "c2", time = 0.0, isParameterized = true),
                    )
                ),
                Chunk(
                    listOf(
                        TestMethod(name = "t4", time = 0.0),
                        TestMethod(name = "t5", time = 0.0),
                        TestMethod(name = "t6", time = 0.0),
                        TestMethod(name = "t7", time = 0.0),
                        TestMethod(name = "t8", time = 0.0),
                        TestMethod(name = "t9", time = 0.0),
                        TestMethod(name = "t10", time = 0.0),
                        TestMethod(name = "c3", time = 0.0, isParameterized = true),
                    )
                )
            )
        ).normalizeLineEnding().trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `should print class per shard with multiple run`() {
        val expected = """
            3 parameterized classes / 2 shards
            Running 10x
              20 total shards
              30 total parameterized classes
        """.trimIndent()

        val result = createMock(10).beforeRunMessage(
            listOf(
                Chunk(
                    listOf(
                        TestMethod(name = "c1", time = 0.0, isParameterized = true),
                        TestMethod(name = "c2", time = 0.0, isParameterized = true),
                    )
                ),
                Chunk(
                    listOf(
                        TestMethod(name = "c3", time = 0.0, isParameterized = true),
                    )
                )
            )
        ).normalizeLineEnding().trimIndent()

        assertEquals(expected, result)
    }
}
