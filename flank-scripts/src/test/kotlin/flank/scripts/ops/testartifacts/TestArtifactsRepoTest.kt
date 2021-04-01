package flank.scripts.ops.testartifacts

import flank.scripts.data.github.getRelease
import flank.scripts.utils.getEnv
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test

class TestArtifactsRepoTest {

    @Test
    fun `should not throw when gh token not set`() {
        mockkStatic(::getEnv) {
            every { getEnv(any(), any()) } returns ""
            testArtifactsRepo().getRelease("master")
                ?.body()?.toLong()
        }
    }
}
