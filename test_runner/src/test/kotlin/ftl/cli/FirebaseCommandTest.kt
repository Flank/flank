package ftl.cli

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class FirebaseCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun firebaseCommandPrintsHelp() {
        FirebaseCommand().run()
        val output = systemOutRule.log
        assertThat(output).startsWith(
            "firebase [COMMAND]\n" +
                "Commands:\n" +
                "  test\n" +
                "  cancel  Cancels the last Firebase Test Lab run\n" +
                "  doctor  Verifies flank firebase is setup correctly\n"
        )
    }
}
