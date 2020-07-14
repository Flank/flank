package ftl.cli.firebase.test.android.models

import com.google.common.truth.Truth
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class AndroidModelsCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidModelsCommandPrintsHelp() {
        AndroidModelsCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        Truth.assertThat(output).startsWith(
            "Information about available models\n\n" +
            "models [COMMAND]\n\n" +
            "Description:\n\n" +
            "Information about available models. For example prints list of available models\n" +
                    "to test against\n" +
                    "Commands:\n" +
                    "  list  Print current list of devices available to test against"
        )
    }
}
