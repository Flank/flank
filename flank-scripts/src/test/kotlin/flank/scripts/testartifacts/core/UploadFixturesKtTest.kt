package flank.scripts.testartifacts.core

import flank.scripts.testartifacts.testContext
import org.junit.Ignore
import org.junit.Test

@Ignore // FIXME
class UploadFixturesKtTest {

    @Test
    fun uploadFixturesTest() {
        println(Runtime.getRuntime().maxMemory() / 1_000_000.0)
        println(Runtime.getRuntime().freeMemory() / 1_000_000.0)
        println(Runtime.getRuntime().totalMemory() / 1_000_000.0)
        testContext.uploadFixtures()
    }
}
