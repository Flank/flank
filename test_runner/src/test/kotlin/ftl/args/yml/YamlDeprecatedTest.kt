package ftl.args.yml

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper
import ftl.run.exception.FlankConfigurationError
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import java.io.StringReader

@RunWith(FlankTestRunner::class)
class YamlDeprecatedTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `Valid YAML`() {
        val input = """
            ---
            gcloud:
              app: "a"
              test: "b"
            flank: {}

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(input)
    }

    @Test
    fun `Inserts missing parents`() {
        val input = ""

        val expected = """
            ---
            gcloud: {}
            flank: {}

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(expected)
    }

    @Test
    fun `Transform missing Flank object node`() {
        // Verify input with null flank ObjectNode is successfully replaced
        val input = """
            ---
            gcloud:
              project: 0

        """.trimIndent()

        val expected = """
            ---
            gcloud: {}
            flank:
              project: 0

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(expected)
    }

    @Test
    fun `Old keys renamed to new keys`() {
        val input = """
            ---
            gcloud:
              project: 0
              flaky-test-attempts: 1

            flank:
              testShards: 1
              shardTime: 2
              repeatTests: 3
              smartFlankGcsPath: 4
              disableSharding: 5
              

        """.trimIndent()

        val expected = """
            ---
            gcloud:
              num-flaky-test-attempts: 1
            flank:
              project: 0
              max-test-shards: 1
              shard-time: 2
              num-test-runs: 3
              smart-flank-gcs-path: 4
              disable-sharding: 5

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(expected)
    }

    @Test
    fun `repeat-tests is renamed`() {
        val input = """
            ---
            gcloud: {}
            flank:
              repeat-tests: 3

        """.trimIndent()

        val expected = """
            ---
            gcloud: {}
            flank:
              num-test-runs: 3

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(expected)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw FlankFatalError when yaml is bad formatted`() {
        YamlDeprecated.modify(
            TestHelper.getPath("src/test/kotlin/ftl/args/yml/test_error_yaml_cases/flank-bad-yaml-formatting.yml")
        )
    }
}

private fun YamlDeprecated.modify(yamlData: String): Pair<Boolean, String> = modify(StringReader(yamlData))
