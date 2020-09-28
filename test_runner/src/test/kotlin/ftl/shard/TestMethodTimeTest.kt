package ftl.shard

import ftl.util.FlankTestMethod
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TestMethodTimeTest {

    @Test
    fun `should use class test time for parametrized tests`() {
        val expected = DEFAULT_CLASS_TEST_TIME_SEC
        val actual = getTestMethodTime(
            FlankTestMethod(testName = "test#Parametrized1", isParameterizedClass = true),
            emptyMap(), DEFAULT_TEST_TIME_SEC, expected
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `should use test time for parametrized tests`() {
        val expected = DEFAULT_TEST_TIME_SEC
        val actual = getTestMethodTime(
            FlankTestMethod(testName = "test#NotParametrized1"),
            emptyMap(), expected, DEFAULT_CLASS_TEST_TIME_SEC
        )
        assertEquals(expected, actual)
    }
}
