package flank.scripts.pullrequest

import flank.scripts.FuelTestRunner
import flank.scripts.testAssignees
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class SetAssigneesTest {

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun `Should properly get labels and set them to pull request`() {
        runBlocking {
            // given
            val expected = testAssignees.map { it.login }

            // when
            copyAssignees("success", 1, 2)

            // then
            assertTrue(systemOutRule.log.contains("$expected set to pull request #2"))
        }
    }

    @Test
    fun `Should not set labels to pull request if get labels failed`() {
        runBlocking {
            // when
            copyAssignees("failure", 1, 2)

            // then
            assertTrue(systemOutRule.log.contains("Could not copy assignees because of "))
        }
    }
}
