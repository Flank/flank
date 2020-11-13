package flank.scripts.pullrequest

import flank.scripts.FuelTestRunner
import flank.scripts.testZenHubIssue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class ZenHubCopyEstimationsTest {

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun `Should properly get estimate and set it to pull request`() {
        runBlocking {
            // given
            val expected = testZenHubIssue.estimate.value
            val pullRequestNumber = 2

            // when
            copyEstimation("success", 1, pullRequestNumber)

            // then
            assertTrue(systemOutRule.log.contains("Estimate $expected set to pull request #$pullRequestNumber"))
        }
    }

    @Test
    fun `Should not set estimate to pull request if get estimate failed`() {
        runBlocking {
            // when
            copyEstimation("failure", 1, 2)

            // then
            assertTrue(systemOutRule.log.contains("Could not get estimations because of "))
            assertTrue(systemOutRule.log.contains("Could not copy estimation because it is not provided"))
        }
    }
}
