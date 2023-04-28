package flank.scripts.ops.github

import com.github.kittinunf.result.map
import flank.scripts.data.github.getLatestReleaseTag
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class DownloadFlankTest {

    @Test
    fun `Should return latest tag if version is null`() {
        val downloadedVersion = runBlocking { null.getVersion(token) }
        val latestVersion = runBlocking { getLatestReleaseTag(token).map { it.tag }.get() }
        Assert.assertEquals(downloadedVersion, latestVersion)
    }

    @Test
    fun `Should return latest tag if version is empty`() {
        val downloadedVersion = runBlocking { "".getVersion(token) }
        val latestVersion = runBlocking { getLatestReleaseTag(token).map { it.tag }.get() }
        Assert.assertEquals(downloadedVersion, latestVersion)
    }

    @Test
    fun `Should return specified release tag`() {
        val expectedReleaseTag = "v.1.1.1"

        val downloadedVersion = runBlocking { expectedReleaseTag.getVersion(token) }

        Assert.assertEquals(downloadedVersion, expectedReleaseTag)
    }

    companion object {
        private const val token = ""
    }
}
