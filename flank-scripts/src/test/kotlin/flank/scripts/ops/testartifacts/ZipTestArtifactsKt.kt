package flank.scripts.ops.testartifacts

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ZipTestArtifactsKt {

    @Before
    fun setUp() {
        testContext.run {
            prepareTestDirectory()
        }
    }

    @After
    fun tearDown() {
        testContext.run {
            removeTestDirectory()
            removeTestArchive()
        }
    }

    @Test
    fun `should zip artifacts directory and than unzip the archive`() {
        testContext.run {
            // when
            zipTestArtifacts()

            // then
            assertNotNull(
                projectRoot.testArtifacts.listFiles()?.find { file ->
                    file.name.contains(branch) && file.extension == "zip"
                }
            )
            removeTestDirectory()

            // when
            unzipTestArtifacts()

            // then
            projectRoot.testArtifacts(branch).run {
                assertTrue(exists())
                assertTrue(isDirectory)
                resolve(TEST_FILE_NAME).run {
                    assertEquals(TEST_FILE_BODY, readText())
                }
            }
        }
    }
}
