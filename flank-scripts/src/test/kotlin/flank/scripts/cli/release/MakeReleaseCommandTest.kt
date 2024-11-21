package flank.scripts.cli.release

import flank.scripts.cli.github.MakeReleaseCommand
import flank.scripts.ops.github.releaseFlank
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit

class MakeReleaseCommandTest {

    @Rule
    @JvmField
    val systemExit = ExpectedSystemExit.none()!!

    @Test
    fun `Should return with exit code 1 when snapshot and no commit hash specified`() {
        // expect
        systemExit.expectSystemExitWithStatus(1)

        // when
        MakeReleaseCommand.main(listOf("--input-file=./", "--git-tag=T", "--snapshot"))
    }

    @Test
    fun `Should return with exit code 1 when not snapshot and no github token specified`() {
        // expect
        systemExit.expectSystemExitWithStatus(1)

        // when
        MakeReleaseCommand.main(listOf("--input-file=./", "--git-tag=T"))
    }

    @Test
    fun `Should return successfully run release for snapshot`() {
        // given
        mockkStatic("flank.scripts.ops.github.ReleaseFlankKt")
        every { releaseFlank(any(), any(), any(), any(), any()) } returns 0

        // expect
        systemExit.expectSystemExitWithStatus(0)

        // when
        MakeReleaseCommand.main(listOf("--input-file=./", "--git-tag=T", "--commit-hash=X", "--snapshot"))
    }

    @Test
    fun `Should return successfully run release for stable`() {
        // given
        mockkStatic("flank.scripts.ops.github.ReleaseFlankKt")
        every { releaseFlank(any(), any(), any(), any(), any()) } returns 0

        // expect
        systemExit.expectSystemExitWithStatus(0)

        // when
        MakeReleaseCommand.main(listOf("--input-file=./", "--git-tag=T", "--token=X"))
    }
}
