package ftl.args.yml

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

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

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(input)
    }

    @Test
    fun `Flank old keys renamed to new keys`() {
        val input = """
            ---
            flank:
              testShards: 1
              shardTime: 2
              repeatTests: 3
              smartFlankGcsPath: 4
              disableSharding: 5


        """.trimIndent()

        val expected = """
            ---
            flank:
              max-test-shards: 1
              shard-time: 2
              repeat-tests: 3
              smart-flank-gcs-path: 4
              disable-sharding: 5

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(expected)
    }
}
