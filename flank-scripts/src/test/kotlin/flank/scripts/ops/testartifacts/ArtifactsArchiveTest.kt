package flank.scripts.ops.testartifacts

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ArtifactsArchiveTest {

    @Before
    fun setUp() {
        testContext.projectRoot.testArtifacts.resolve(zipFileName).createNewFile()
    }

    @After
    fun tearDown() {
        testContext.projectRoot.testArtifacts.resolve(zipFileName).delete()
    }

    @Test
    fun `get latest artifacts archive name`() {
        testContext.latestArtifactsArchive()!!.run {
            assertEquals(zipFileName, name)
        }
    }

    @Test
    fun `should parse artifacts archive by name`() {
        zipFileName.parseArtifactsArchive().run {
            assertEquals(zipFileName, fullName)
            assertEquals(assetName, shortName)
        }
    }

    private companion object {
        const val zipFileName = "$TEST_BRANCH_1-123.zip"
        const val assetName = "123.zip"
    }
}
