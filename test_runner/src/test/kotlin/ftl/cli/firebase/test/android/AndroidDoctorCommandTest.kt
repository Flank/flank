package ftl.cli.firebase.test.android

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class AndroidDoctorCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidDoctorCommandPrintsHelp() {
        val doctor = AndroidDoctorCommand()
        assertThat(doctor.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(doctor, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
            "Verifies flank firebase is setup correctly\n" +
                "\n" +
                "doctor [-h] [-c=<configPath>]\n" +
                "\n" +
                "Description:\n" +
                "\n" +
                "Validates Android Flank YAML.\n" +
                "\n" +
                "\n" +
                "Options:\n" +
                "  -c, --config=<configPath>\n" +
                "               YAML config file path\n" +
                "  -h, --help   Prints this help message\n"
        )

        assertThat(doctor.usageHelpRequested).isTrue()
    }

    @Test
    fun androidDoctorCommandRuns() {
        AndroidDoctorCommand().run()
        // When there are no lint errors, output is a newline.
        val output = systemOutRule.log
        Truth.assertThat(output).isEqualTo("\n")
    }
}
