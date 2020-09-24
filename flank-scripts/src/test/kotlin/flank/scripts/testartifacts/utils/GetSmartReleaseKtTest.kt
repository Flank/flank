package flank.scripts.testartifacts.utils

import org.junit.Test

class GetSmartReleaseKtTest {

    @Test
    fun test() {
        getOrCreateArtifactsRelease("test")
        getOrCreateArtifactsRelease("test").delete()
    }
}
