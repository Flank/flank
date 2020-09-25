package flank.scripts.testartifacts.utils

import org.junit.Test
import java.io.File

class PrepareTestArtifactsKtTest {

    @Test
    fun prepareTestArtifacts() {
        prepareTestArtifacts(File("../"))
    }
}
