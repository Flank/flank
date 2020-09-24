package flank.scripts.testartifacts.utils

import org.junit.Test

import org.junit.Assert.*
import java.io.File

class PrepareTestArtifactsKtTest {

    @Test
    fun prepareTestArtifacts() {
        prepareTestArtifacts(File("../"))
    }
}
