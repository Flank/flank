package flank.scripts.testartifacts.utils

import org.junit.Test
import java.io.File

class ZipTestArtifactsKtTest {

    @Test
    fun zipTestArtifacts() {
        zipTestArtifacts(File("../"))
    }

    @Test
    fun unzipTestArtifacts() {
        unzipTestArtifacts(File("../"))
    }
}
