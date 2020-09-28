package flank.scripts.testartifacts.core

import flank.scripts.testartifacts.testContext
import org.junit.Ignore
import org.junit.Test

@Ignore // FIXME
class LinkArtifactsKtTest {

    @Test
    fun linkCurrentTest() {
        testContext.linkArtifacts()
    }

    @Test
    fun linkMasterTest() {
        testContext.copy(branch = "master").linkArtifacts()
    }
}
