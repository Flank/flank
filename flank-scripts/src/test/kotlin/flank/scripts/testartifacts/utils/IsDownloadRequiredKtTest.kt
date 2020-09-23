package flank.scripts.testartifacts.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class IsDownloadRequiredKtTest {

    @Test
    fun test() {
        fetchRemoteAssetLink().run {
            assertTrue(isDownloadRequired(""))
            assertFalse(isDownloadRequired(FIXTURES_PATH)) // FIXME find better way to test negative path
        }
    }
}
