package ftl.util

import com.google.common.truth.Truth.assertThat
import flank.common.isMacOS
import flank.common.isWindows
import ftl.api.Command
import ftl.api.runCommand
import ftl.run.exception.FlankGeneralError
import ftl.test.util.FlankTestRunner
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class BashTest {

    @Test
    fun executeStderr() {
        assumeFalse(isWindows)
        assertThat(runCommand(Command("echo a 1>&2"))).isEqualTo("a")
    }

    @Test(expected = FlankGeneralError::class)
    fun executeStderrExitCode1() {
        if (isWindows) throw FlankGeneralError("Failure as is windows")
        assertThat(runCommand(Command("echo not an error 1>&2; exit 1"))).isEmpty()
    }

    @Test
    fun executeNoOutput() {
        assumeFalse(isWindows)
        assertThat(runCommand(Command(" "))).isEmpty()
    }

    @Test
    fun executeSmallOutput() {
        assumeFalse(isWindows)
        assertThat(runCommand(Command("echo ok"))).isEqualTo("ok")
    }

    @Test
    fun executeLargeOutput() {
        assumeFalse(isWindows)
        // gohello is a binary that outputs 100k 'hi' to stdout
        val os = if (isMacOS) {
            "mac"
        } else {
            "linux"
        }
        val cmd = "./src/test/kotlin/ftl/fixtures/tmp/gohello/bin/$os/gohello"
        // ensure binary is marked executable. unzip may have removed the executable bit.
        File(cmd).setExecutable(true)
        assertThat(runCommand(Command(cmd)).length).isEqualTo(200_000)
    }
}
