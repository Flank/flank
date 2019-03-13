package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class IosDoctorCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosDoctorCommandPrintsHelp() {
        val doctor = IosDoctorCommand()
        assertThat(doctor.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(doctor, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
            "Verifies flank firebase is setup correctly\n" +
                "\n" +
                "doctor [-fh] [-c=<configPath>]\n" +
                "\n" +
                "Description:\n" +
                "\n" +
                "Validates iOS YAML and connection to iOS catalog.\n" +
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
    fun iosDoctorCommandRuns() {
        IosDoctorCommand().run()
    }

    @Test
    fun iosDoctorCommandOptions() {
        val cmd = IosDoctorCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultIosConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()

        assertThat(cmd.fix).isFalse()
        cmd.fix = true
        assertThat(cmd.fix).isTrue()
    }
}
