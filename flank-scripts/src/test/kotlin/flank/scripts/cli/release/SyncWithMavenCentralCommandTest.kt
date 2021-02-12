package flank.scripts.cli.release

import flank.scripts.ops.jfrog.jFrogSync
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit

class SyncWithMavenCentralCommandTest {

    @Rule
    @JvmField
    val systemExit = ExpectedSystemExit.none()!!

    @Test
    fun `Should return same exit code as command`() {
        // given
        mockkStatic(::jFrogSync)
        every { jFrogSync(any()) } returns 1

        // expect
        systemExit.expectSystemExitWithStatus(1)

        // when
        SyncWithMavenCentralCommand.main(listOf("--git-tag=TAG"))

        // then
        verify { jFrogSync("TAG") }
    }
}
