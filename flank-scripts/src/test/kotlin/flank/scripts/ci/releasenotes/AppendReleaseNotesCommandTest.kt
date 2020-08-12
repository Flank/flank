package flank.scripts.ci.releasenotes

import com.google.common.truth.Truth.assertThat
import java.nio.file.Paths
import org.junit.Test

class AppendReleaseNotesCommandTest {

    @Test
    fun `Should not used default file if passed other as argument`() {
        // given
        val expected = Paths.get("").toAbsolutePath().toString()

        // when
        val command = AppendReleaseNotesCommand().apply {
            main(listOf(
                "--pr-number=1",
                "--git-user=test",
                "--merged-pr-title=title",
                "--release-notes-file=$expected")
            )
        }

        // then
        assertThat(command.releaseNotesFile).isEqualTo(expected)
    }
}
