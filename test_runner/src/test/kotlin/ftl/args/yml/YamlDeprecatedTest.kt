package ftl.args.yml

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class YamlDeprecatedTest {

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
    fun `Flank testShards renamed to maxTestShards`() {
        val input = """
            ---
            flank:
              testShards: 1

        """.trimIndent()

        val expected = """
            ---
            flank:
              maxTestShards: 1

        """.trimIndent()

        val (error, output) = YamlDeprecated.modify(input)

        assertThat(error).isFalse()
        assertThat(output).isEqualTo(expected)
    }
}
