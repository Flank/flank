package ftl.run

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.test.util.FlankTestRunner
import ftl.test.util.ios2ConfigYaml
import ftl.test.util.mixedConfigYaml
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
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
        "class com.example.test_app.InstrumentedTest#test"
      ]
    },
    "junit-ignored": [
      "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
      "class com.example.test_app.InstrumentedTest#ignoredTestWithSuppress"
    ]
  },
  "matrix-1": {
    "app": "$path/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
    "test": "$path/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-flaky-debug-androidTest.apk",
    "shards": {
      "shard-0": [
        "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
        "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized",
        "class com.example.test_app.InstrumentedTest#test1",
        "class com.example.test_app.InstrumentedTest#test2"
      ],
      "shard-1": [
        "class com.example.test_app.ParameterizedTest",
        "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner",
        "class com.example.test_app.InstrumentedTest#test0",
        "class com.example.test_app.bar.BarInstrumentedTest#testBar",
        "class com.example.test_app.foo.FooInstrumentedTest#testFoo"
      ]
    },
    "junit-ignored": [
      "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
      "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
      "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
      "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
    ]
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
    fun `dump shards obfuscated android`() {
        // given
        val notExpected = """
{
  "matrix-0": {
    "app": "/pathToApk/app-debug.apk",
    "test": "/pathToApk/app-single-success-debug-androidTest.apk",
    "shards": {
      "shard-0": [
        "class com.example.test_app.InstrumentedTest#test"
      ]
    },
    "junit-ignored": [
      "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
      "class com.example.test_app.InstrumentedTest#ignoredTestWithSuppress"
    ]
  },
  "matrix-1": {
    "app": "/pathToApk/app-debug.apk",
    "test": "/pathToApk/app-multiple-flaky-debug-androidTest.apk",
    "shards": {
      "shard-0": [
        "class com.example.test_app.InstrumentedTest#test1",
        "class com.example.test_app.InstrumentedTest#test2",
        "class com.example.test_app.ParameterizedTest",
        "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed"
      ],
      "shard-1": [
        "class com.example.test_app.InstrumentedTest#test0",
        "class com.example.test_app.bar.BarInstrumentedTest#testBar",
        "class com.example.test_app.foo.FooInstrumentedTest#testFoo",
        "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized",
        "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner"
      ]
    },
    "junit-ignored": [
      "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
      "class com.example.test_app.InstrumentedTest#ignoredTestWithSuppress",
      "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
      "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
    ]
  }
}
        """.trimIndent()

        // when
        val actual = runBlocking {
            dumpShards(AndroidArgs.load(mixedConfigYaml), TEST_SHARD_FILE, true)
            File(TEST_SHARD_FILE).apply { deleteOnExit() }.readText()
        }

        // then
        assertNotEquals(notExpected, actual)
        assertThat(notExpected.split(System.lineSeparator()).size).isEqualTo(actual.split(System.lineSeparator()).size) // same line count
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

    @Test
    fun `dump shards obfuscated ios`() {
        // given
        val notExpected = """
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
            dumpShards(IosArgs.load(ios2ConfigYaml), TEST_SHARD_FILE, true)
            File(TEST_SHARD_FILE).apply { deleteOnExit() }.readText()
        }

        // then
        assertNotEquals(notExpected, actual)
        assertThat(notExpected.split(System.lineSeparator()).size).isEqualTo(actual.split(System.lineSeparator()).size) // same line count
    }
}

private const val TEST_SHARD_FILE = "test_dump_shard_file.json"
