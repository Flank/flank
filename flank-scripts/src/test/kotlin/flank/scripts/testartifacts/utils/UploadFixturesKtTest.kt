package flank.scripts.testartifacts.utils

import org.junit.Test

class UploadFixturesKtTest {

    @Test
    fun uploadFixturesTest() {
        println(Runtime.getRuntime().maxMemory() / 1_000_000.0)
        println(Runtime.getRuntime().freeMemory() / 1_000_000.0)
        println(Runtime.getRuntime().totalMemory() / 1_000_000.0)
        uploadFixtures()
    }
}
