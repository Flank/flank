package flank.scripts.release.hub

import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit

class ReleaseFlankCommandTest {

    @Rule
    @JvmField
    val systemExit = ExpectedSystemExit.none()!!

    @Test
    fun `Should return same exit code as command`() {
        // given
        mockkStatic("flank.scripts.release.hub.ReleaseFlankKt")
        every { releaseFlank(any(), any(), any(), any()) } returns 1

        // expect
        systemExit.expectSystemExitWithStatus(1)

        // when
        ReleaseFlankCommand().main(listOf("--input-file=./", "--git-tag=T", "--commit-hash=X"))
    }
}
