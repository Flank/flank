package ftl.cli.firebase.test

import ftl.doctor.assertEqualsIgnoreNewlineStyle
import ftl.presentation.cli.firebase.test.IPBlocksCommand
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class IPBlocksCommandTest {

    @get:Rule
    val out = SystemOutRule().enableLog().muteForSuccessfulTests() as SystemOutRule

    @Test
    fun printHelp() {
        IPBlocksCommand().run()

        val expected = """
Explore IP blocks used by Firebase Test Lab devices.
ip-blocks [COMMAND]
Commands:
  list  List all IP address blocks used by Firebase Test Lab devices
        """.trimIndent()

        val actual = out.log.trim()

        assertEqualsIgnoreNewlineStyle(expected, actual)
    }
}
