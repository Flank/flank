package ftl.cli.firebase.test

import flank.common.normalizeLineEnding
import ftl.presentation.cli.firebase.test.NetworkProfilesCommand
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class NetworkProfilesCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun printHelp() {
        NetworkProfilesCommand().run()

        val expected = listOf(
            "Explore network profiles available for testing.",
            "network-profiles [COMMAND]",
            "Commands:",
            "  list      List all network profiles available for testing",
            "  describe  Describe a network profile",
            ""
        ).joinToString("\n")

        val actual = systemOutRule.log.normalizeLineEnding()

        assertEquals(expected, actual)
    }
}
