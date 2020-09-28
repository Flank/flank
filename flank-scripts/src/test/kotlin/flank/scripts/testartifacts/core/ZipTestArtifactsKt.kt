package flank.scripts.testartifacts.core

import flank.scripts.testartifacts.testContext
import org.junit.Ignore
import org.junit.Test

@Ignore // FIXME
class ZipTestArtifactsKt {

    @Test
    fun zipTestArtifacts() {
        testContext.zipTestArtifacts()
    }

    @Test
    fun unzipTestArtifacts() {
        testContext.unzipTestArtifacts()
    }
}
