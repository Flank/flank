package flank.scripts.testartifacts.utils

import org.junit.Test

import org.junit.Assert.*

class ZipTestArtifactsKtTest {

    @Test
    fun zipTestArtifacts() {
        zipTestArtifacts("../")
    }

    @Test
    fun unzipTestArtifacts() {
        unzipTestArtifacts("../")
    }
}
