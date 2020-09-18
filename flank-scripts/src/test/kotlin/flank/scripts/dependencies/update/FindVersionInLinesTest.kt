package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Test

class FindVersionInLinesTest {

    @Test
    fun `should find correct version in lines`() {
        // given
        val expected = "KTOR"
        val testLines = """
                const val KTOR_GSON = "io.ktor:ktor-gson:${'$'}{Versions.KTOR}"
    const val KTOR_SERVER_CORE = "io.ktor:ktor-server-core:${'$'}{Versions.KTOR}"
    const val KTOR_SERVER_NETTY = "io.ktor:ktor-server-netty:${'$'}{Versions.KTOR}"
    const val LOGBACK = "ch.qos.logback:logback-classic:${'$'}{Versions.LOGBACK}"
        """.trimIndent().split("\n")

        // when
        val actual = testLines.matchingVersionVal("io.ktor:ktor-gson")

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should not find correct version in lines`() {
        // given
        val expected = "!versionNotFound"
        val testLines = """
                const val KTOR_GSON = "io.ktor:ktor-gson:${'$'}{Versions.KTOR}"
    const val KTOR_SERVER_CORE = "io.ktor:ktor-server-core:${'$'}{Versions.KTOR}"
    const val KTOR_SERVER_NETTY = "io.ktor:ktor-server-netty:${'$'}{Versions.KTOR}"
    const val LOGBACK = "ch.qos.logback:logback-classic:${'$'}{Versions.LOGBACK}"
        """.trimIndent().split("\n")

        // when
        val actual = testLines.matchingVersionVal("flt.flank:flank-scripts")

        // then
        assertEquals(expected, actual)
    }
}
