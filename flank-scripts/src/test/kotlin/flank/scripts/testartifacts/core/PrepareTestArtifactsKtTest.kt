package flank.scripts.testartifacts.core

import flank.scripts.testartifacts.testContext
import org.junit.Ignore
import org.junit.Test

@Ignore // FIXME
class PrepareTestArtifactsKtTest {

    @Test
    fun prepareTestArtifacts() {
        testContext.prepareTestArtifacts()
    }
}
