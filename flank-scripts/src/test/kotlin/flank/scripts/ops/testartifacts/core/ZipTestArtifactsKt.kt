package flank.scripts.ops.testartifacts.core

import flank.scripts.ops.testartifacts.TEST_FILE_BODY
import flank.scripts.ops.testartifacts.TEST_FILE_NAME
import flank.scripts.ops.testartifacts.prepareTestDirectory
import flank.scripts.ops.testartifacts.removeTestArchive
import flank.scripts.ops.testartifacts.removeTestDirectory
import flank.scripts.ops.testartifacts.testArtifacts
import flank.scripts.ops.testartifacts.testContext
import flank.scripts.ops.testartifacts.unzipTestArtifacts
import flank.scripts.ops.testartifacts.zipTestArtifacts
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
