package flank.scripts.ops.common

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ConventionalCommitFormatterTest(
    private val mapFrom: String,
    private val expectedType: String?,
    private val expectedTitle: String?
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} to {1}")
        fun data() = listOf(
            arrayOf("feat: Sample", "Features", "Sample"),
            arrayOf("fix: Bug", "Bug Fixes", "Bug"),
            arrayOf("docs: New", "Documentation", "New"),
            arrayOf("refactor: Everything", "Refactor", "Everything"),
            arrayOf("ci(cd): actions", "CI Changes", "Actions"),
            arrayOf("test(ios): update", "Tests update", "Update"),
            arrayOf("perf!: 10%", "Performance upgrade", "10%"),
            arrayOf("style: dd", null, null),
            arrayOf("build: passed", null, null),
            arrayOf("chore: release", null, null),
            arrayOf("nope: nbd", null, null)
        )
    }

    @Test
    fun `Should properly map`() {
        // when
        val actual = mapFrom.mapPrTitleWithType()

        // then
        if (expectedType != null) {
            val (type, title) = actual!!
            assertThat(type).isEqualTo(expectedType)
            assertThat(title).startsWith(expectedTitle)
        } else {
            assertThat(actual).isNull()
        }
    }
}
