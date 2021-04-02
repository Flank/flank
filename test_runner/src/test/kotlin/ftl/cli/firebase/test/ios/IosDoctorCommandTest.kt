package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.cli.firebase.test.INVALID_YML_PATH
import ftl.cli.firebase.test.SUCCESS_VALIDATION_MESSAGE
import ftl.config.FtlConstants
import ftl.presentation.cli.firebase.test.ios.IosDoctorCommand
import ftl.run.exception.YmlValidationError
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
        CommandLine(doctor).execute("-h")

        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
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
                "  -h, --help   Prints this help message\n"
        )

        assertThat(doctor.usageHelpRequested).isTrue()
    }

    @Test
    fun iosDoctorCommandRuns() {
        IosDoctorCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).isEqualTo(SUCCESS_VALIDATION_MESSAGE)
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

    @Test(expected = YmlValidationError::class)
    fun `should terminate with exit code 1 when yml validation fails`() {
        IosDoctorCommand().run {
            configPath = INVALID_YML_PATH
            run()
        }
    }

    @Test
    fun `ios doctor should not fail on local-result-dir`() {
        IosDoctorCommand().apply {
            configPath = FtlConstants.defaultIosConfig
        }.run()
    }
}
