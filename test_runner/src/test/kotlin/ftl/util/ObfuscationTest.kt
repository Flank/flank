package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertNotEquals
import org.junit.Test

internal class ObfuscationTest {

    @Test
    fun `should obfuscate android like test method string`() {
        // given
        val testString = "com.flank.Super#test"
        val obfuscationContext: ObfuscationContext = mutableMapOf()

        // when
        val obfuscated = obfuscationContext.obfuscateAndroidTestName(testString)

        // then
        assertNotEquals(testString, obfuscated)
    }

    @Test
    fun `should obfuscate ios like test method string`() {
        // given
        val obfuscationContext: ObfuscationContext = mutableMapOf()
        val testString = "Flank/SuperTest"

        // when
        val obfuscated = obfuscationContext.obfuscateIosTestName(testString)

        // then
        assertNotEquals(testString, obfuscated)
    }

    @Test
    fun `should obfuscate android method name and assign unique symbols to package and method`() {
        // given
        val testMethods = listOf(
            "com.example.Class1#method1",
            "com.example.Class1#method2",
            "com.example.Class2#method1",
            "com.example.foo.Class1#method1 ",
            "flank.support.Test#test2",
            "flank.test.Test#test1"
        )
        val expectedObfuscationResult = listOf(
            "a.a.A#a",
            "a.a.A#b",
            "a.a.B#a",
            "a.a.a.A#a",
            "b.a.A#a",
            "b.b.A#a"
        )
        val obfuscationContext: ObfuscationContext = mutableMapOf()

        // when
        val testResults = testMethods.map { obfuscationContext.obfuscateAndroidTestName(it) }

        // then
        testResults.forEachIndexed { index, obfuscatedMethodName ->
            assertThat(obfuscatedMethodName).isEqualTo(expectedObfuscationResult[index])
        }
    }

    @Test
    fun `should obfuscate android only package and class if method not present`() {
        // given
        val testMethods = listOf(
            "com.example.Class1",
            "com.example.Class1",
            "com.example.Class2#method1",
            "com.example.foo.Class1",
            "flank.support.Test",
            "flank.test.Test"
        )
        val expectedObfuscationResult = listOf(
            "a.a.A",
            "a.a.A",
            "a.a.B#a",
            "a.a.a.A",
            "b.a.A",
            "b.b.A"
        )
        val obfuscationContext: ObfuscationContext = mutableMapOf()

        // when
        val testResults = testMethods.map { obfuscationContext.obfuscateAndroidTestName(it) }

        // then
        testResults.forEachIndexed { index, obfuscatedMethodName ->
            assertThat(obfuscatedMethodName).isEqualTo(expectedObfuscationResult[index])
        }
    }

    @Test
    fun `should obfuscate ios method name and assign unique symbols to class and method`() {
        // given
        val testMethods = listOf(
            "SampleIOS/test",
            "SampleIOS/test2",
            "SampleIOS/test3",
            "SampleIOS/test4",
            "SampleIOS1/test",
            "SampleIOS1/test2"
        )
        val expectedObfuscationResult = listOf(
            "A/a",
            "A/b",
            "A/c",
            "A/d",
            "B/a",
            "B/b"
        )
        val obfuscationContext: ObfuscationContext = mutableMapOf()

        // when
        val testResults = testMethods.map { obfuscationContext.obfuscateIosTestName(it) }

        // then
        testResults.forEachIndexed { index, obfuscatedMethodName ->
            assertThat(obfuscatedMethodName).isEqualTo(expectedObfuscationResult[index])
        }
    }
}
