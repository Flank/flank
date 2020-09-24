package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import org.junit.Test

class LinkArtifactsKtTest {

    @Test
    fun linkCurrentTest() {
        println(currentGitBranch())
        linkArtifacts("../")
    }

    @Test
    fun linkMasterTest() {
        linkArtifacts("../", "master")
    }
}
