package flank.scripts.testartifacts.utils

import org.junit.Test

import org.junit.Assert.*

class UploadFixturesKtTest {

    @Test
    fun uploadFixturesTest() {
        println(Runtime.getRuntime().maxMemory())
        uploadFixtures()
    }
}
