package ftl.cli.firebase.test.android.versions

import com.google.common.truth.Truth
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class AndroidVersionsCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosModelsCommandPrintsHelp() {
        AndroidVersionsCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        Truth.assertThat(output).startsWith(
            "Information about available software versions\n\n" +
                    "versions [COMMAND]\n\n" +
                    "Description:\n\n" +
                    "Information about available software versions. For example prints list of\n" +
                    "available software versions\n" +
                    "Commands:\n" +
                    "  list  List of OS versions available to test against"
        )
    }
}
