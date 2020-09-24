package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import org.junit.Test
import java.io.File

class LinkArtifactsKtTest {

    @Test
    fun linkCurrentTest() {
        println(currentGitBranch())
        linkArtifacts(File("../"))
    }

    @Test
    fun linkMasterTest() {
        linkArtifacts(File("../"), "master")
    }
}
