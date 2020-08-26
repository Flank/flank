//package ftl.run
//
//import ftl.args.IArgs
//import ftl.run.platform.common.beforeRunMessage
//import ftl.test.util.FlankTestRunner
//import ftl.test.util.TestHelper.assert
//import ftl.test.util.TestHelper.normalizeLineEnding
//import ftl.util.trimStartLine
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.unmockkAll
//import org.junit.After
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(FlankTestRunner::class)
//class GenericTestRunnerTest {
//
//    private fun createMock(repeatTests: Int) = mockk<IArgs>().apply {
//        every { this@apply.repeatTests } returns repeatTests
//    }
//
//    @After
//    fun tearDown() = unmockkAll()
//
//    @Test
//    fun testBeforeRunMessage1() {
//        val result = beforeRunMessage(createMock(1), listOf(listOf(""))).normalizeLineEnding()
//        assert(result, "  1 test / 1 shard\n")
//    }
//
//    @Test
//    fun testBeforeRunMessage2() {
//        val result = beforeRunMessage(createMock(2), listOf(listOf(""))).normalizeLineEnding()
//        assert(
//            result, """
//  1 test / 1 shard
//  Running 2x
//    2 total shards
//    2 total tests
//""".trimStartLine()
//        )
//    }
//
//    @Test
//    fun testBeforeRunMessage3() {
//        val result = beforeRunMessage(
//            createMock(2),
//            listOf(listOf(""), listOf(""), listOf(""), listOf(""), listOf(""), listOf(""))
//        ).normalizeLineEnding()
//        assert(
//            result, """
//  6 tests / 6 shards
//  Running 2x
//    12 total shards
//    12 total tests
//""".trimStartLine()
//        )
//    }
//
//    @Test
//    fun testBeforeRunMessage4() {
//        val result = beforeRunMessage(
//            createMock(100),
//            listOf(
//                listOf("", "", "", "", ""),
//                listOf("", "", "", "", "")
//            )
//        ).normalizeLineEnding()
//        assert(
//            result, """
//  10 tests / 2 shards
//  Running 100x
//    200 total shards
//    1000 total tests
//""".trimStartLine()
//        )
//    }
//
//    @Test
//    fun `should print tests + class per shard`() {
//        val expected = """
//            10 tests + 3 classes / 5 shards
//        """.trimIndent()
//    }
//}
