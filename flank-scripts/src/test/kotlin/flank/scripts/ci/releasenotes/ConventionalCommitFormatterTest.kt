package flank.scripts.ci.releasenotes

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ConventionalCommitFormatterTest(private val mapFrom: String, private val expected: String?) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} to {1}")
        fun data() = listOf(
                arrayOf("feat: Sample", "New feature"),
                arrayOf("fix: Bug", "Fix"),
                arrayOf("docs: New", "Documentation"),
                arrayOf("refactor: Eveerything", "Refactor"),
                arrayOf("ci(cd): actions", "CI changes"),
                arrayOf("test(ios): update", "Tests update"),
                arrayOf("perf!: 10%", "Performance upgrade"),
                arrayOf("style: dd", null),
                arrayOf("build: passed", null),
                arrayOf("chore: release", null),
                arrayOf("nope: nbd", null)
        )
    }

    @Test
    fun `Should properly map`() {
        // given
        val expected = expected?.let { "**$it**" }

        // when
        val actual = mapFrom.mapPrTitle()

        // then
        if (expected != null) {
            assertThat(actual).startsWith(expected)
        } else {
            assertThat(actual).isNull()
        }
    }
}
