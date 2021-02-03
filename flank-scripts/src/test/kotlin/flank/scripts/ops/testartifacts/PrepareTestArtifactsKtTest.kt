package flank.scripts.ops.testartifacts

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PrepareTestArtifactsKtTest {

    @Before
    fun setUp() {
        testContext.prepareTestDirectory()
    }

    @After
    fun tearDown() {
        testContext.run {
            removeTestDirectory()
            copy(branch = TEST_BRANCH_2).removeTestDirectory()
        }
    }

    @Test
    fun `should prepare test artifacts`() {
        testContext.copy(branch = TEST_BRANCH_2).run {
            // when
            prepareTestArtifacts(TEST_BRANCH_1)

            // then
            projectRoot.run {
                assertEquals(
                    testArtifacts(TEST_BRANCH_1).listFiles()!!.map { it.name },
                    testArtifacts(TEST_BRANCH_2).listFiles()!!.map { it.name }
                )
            }
        }
    }
}
