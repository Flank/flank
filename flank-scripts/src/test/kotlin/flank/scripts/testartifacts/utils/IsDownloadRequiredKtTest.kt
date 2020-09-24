package flank.scripts.testartifacts.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class IsDownloadRequiredKtTest {

    @Test
    fun artifactsOutdated() {
        fetchRemoteAssetLink().run {
            assertTrue(artifactsOutdated(branch = "latest"))
            assertFalse(artifactsOutdated(branch = ""))
        }
    }
}
