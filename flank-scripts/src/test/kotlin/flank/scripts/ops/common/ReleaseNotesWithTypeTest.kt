package flank.scripts.ops.common

import org.junit.Assert.assertEquals
import org.junit.Test

class ReleaseNotesWithTypeTest {

    @Test
    fun `Should properly format release notes with type`() {
        // given
        val testTag = "TAG"
        val expected = """
            ## TAG
            ### Features
            - [#1](https://github.com/Flank/flank/pull/1) Tests1 ([test_of](https://github.com/test_of))
            - [#2](https://github.com/Flank/flank/pull/2) Tests2 ([test_of](https://github.com/test_of))
            - [#3](https://github.com/Flank/flank/pull/3) Tests3 ([test_of](https://github.com/test_of))
            ### Bug fixes
            - [#11](https://github.com/Flank/flank/pull/11) Tests11 ([test_of](https://github.com/test_of))
            - [#12](https://github.com/Flank/flank/pull/12) Tests12 ([test_of](https://github.com/test_of))
            - [#13](https://github.com/Flank/flank/pull/13) Tests13 ([test_of](https://github.com/test_of))
            ### Documentation
            - [#21](https://github.com/Flank/flank/pull/21) Tests21 ([test_of](https://github.com/test_of))
            - [#22](https://github.com/Flank/flank/pull/22) Tests22 ([test_of](https://github.com/test_of))
            - [#23](https://github.com/Flank/flank/pull/23) Tests33 ([test_of](https://github.com/test_of))

        """.trimIndent()
        val releaseNotesWithType = mapOf(
            "Features" to listOf(
                "- [#1](https://github.com/Flank/flank/pull/1) Tests1 ([test_of](https://github.com/test_of))",
                "- [#2](https://github.com/Flank/flank/pull/2) Tests2 ([test_of](https://github.com/test_of))",
                "- [#3](https://github.com/Flank/flank/pull/3) Tests3 ([test_of](https://github.com/test_of))"
            ),
            "Bug fixes" to listOf(
                "- [#11](https://github.com/Flank/flank/pull/11) Tests11 ([test_of](https://github.com/test_of))",
                "- [#12](https://github.com/Flank/flank/pull/12) Tests12 ([test_of](https://github.com/test_of))",
                "- [#13](https://github.com/Flank/flank/pull/13) Tests13 ([test_of](https://github.com/test_of))"
            ),
            "Documentation" to listOf(
                "- [#21](https://github.com/Flank/flank/pull/21) Tests21 ([test_of](https://github.com/test_of))",
                "- [#22](https://github.com/Flank/flank/pull/22) Tests22 ([test_of](https://github.com/test_of))",
                "- [#23](https://github.com/Flank/flank/pull/23) Tests33 ([test_of](https://github.com/test_of))"
            )
        )

        // when
        val actual = releaseNotesWithType.asString(testTag)

        // then
        assertEquals(expected, actual)
    }
}
