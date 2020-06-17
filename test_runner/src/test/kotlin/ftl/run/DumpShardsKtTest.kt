package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.test.util.ios2ConfigYaml
import ftl.test.util.mixedConfigYaml
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class DumpShardsKtTest {

    @Test
    fun `dump shards android`() {
        // given
        val path = File("").absolutePath
        val expected = """
{
  "matrix-0": {
    "app": "$path/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
    "test": "$path/src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk",
    "shards": {
      "shard-0": [
        "class com.example.test_app.InstrumentedTest#test",
        "class com.example.test_app.InstrumentedTest#ignoredTest",
        "class com.example.test_app.InstrumentedTest#ignoredTest2"
      ]
    }
  },
  "matrix-1": {
    "app": "$path/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
    "test": "$path/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-flaky-debug-androidTest.apk",
    "shards": {
      "shard-0": [
        "class com.example.test_app.InstrumentedTest#test1",
        "class com.example.test_app.InstrumentedTest#test2",
        "class com.example.test_app.ParameterizedTest",
        "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
        "class com.example.test_app.InstrumentedTest#ignoredTest1",
        "class com.example.test_app.InstrumentedTest#ignoredTest2",
        "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
        "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
      ],
      "shard-1": [
        "class com.example.test_app.InstrumentedTest#test0",
        "class com.example.test_app.bar.BarInstrumentedTest#testBar",
        "class com.example.test_app.foo.FooInstrumentedTest#testFoo",
        "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized",
        "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner"
      ]
    }
  }
}
        """.trimIndent()

        // when
        val actual = runBlocking {
            dumpShards(AndroidArgs.load(mixedConfigYaml), TEST_SHARD_FILE)
            File(TEST_SHARD_FILE).apply { deleteOnExit() }.readText()
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `dump shards ios`() {
        // given
        val expected = """
[
  [
    "EarlGreyExampleSwiftTests/testWithGreyAssertions",
    "EarlGreyExampleSwiftTests/testWithInRoot",
    "EarlGreyExampleSwiftTests/testWithCondition",
    "EarlGreyExampleSwiftTests/testWithCustomFailureHandler"
  ],
  [
    "EarlGreyExampleSwiftTests/testWithGreyAssertions",
    "EarlGreyExampleSwiftTests/testWithCustomMatcher",
    "EarlGreyExampleSwiftTests/testWithCustomAssertion"
  ]
]
        """.trimIndent()

        // when
        val actual = runBlocking {
            dumpShards(IosArgs.load(ios2ConfigYaml), TEST_SHARD_FILE)
            File(TEST_SHARD_FILE).apply { deleteOnExit() }.readText()
        }

        // then
        assertEquals(expected, actual)
    }
}

private const val TEST_SHARD_FILE = "test_dump_shard_file.json"
