package flank.scripts.testartifacts.core

import flank.scripts.github.getOrCreateRelease
import org.junit.Ignore
import org.junit.Test

@Ignore // FIXME
class TestArtifactsRepoKtTest {

    @Test
    fun test() {
        testArtifactsRepo().run {
            getOrCreateRelease("test")
            getOrCreateRelease("test").delete()
        }
    }
}
