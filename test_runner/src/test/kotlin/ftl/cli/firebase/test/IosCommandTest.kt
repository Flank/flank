package ftl.cli.firebase.test

import com.google.common.truth.Truth
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidCommandPrintsHelp() {
        IosCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        Truth.assertThat(output).startsWith(
            "ios [COMMAND]\n" +
                "Commands:\n" +
                "  run     Run tests on Firebase Test Lab\n" +
                "  doctor  Verifies flank firebase is setup correctly\n"
        )
    }
}
