package flank.scripts.ops.ci.releasenotes

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class GenerateReleaseNotesCommandTest {

    @Test
    fun `Should not used default file if passed other as argument`() {
        // given
        val expected = File.createTempFile("test", ".file").absolutePath

        // when
        val command = GenerateReleaseNotesCommand().apply {
            main(
                listOf(
                    "--token=token",
                    "--release-notes-file=$expected"
                )
            )
        }

        // then
        assertThat(command.releaseNotesFile).isEqualTo(expected)
    }
}
