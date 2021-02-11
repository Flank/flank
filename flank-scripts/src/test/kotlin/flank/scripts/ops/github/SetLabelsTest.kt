package flank.scripts.ops.github

import flank.scripts.FuelTestRunner
import flank.scripts.testGithubLabels
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class SetLabelsTest {

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun `Should properly get labels and set them to pull request`() {
        runBlocking {
            // given
            val expected = testGithubLabels.map { it.name }

            // when
            copyLabels("success", 1, 2)

            // then
            assertTrue(systemOutRule.log.contains("$expected set to pull request #2"))
        }
    }

    @Test
    fun `Should not set labels to pull request if get labels failed`() {
        runBlocking {

            // when
            copyLabels("failure", 1, 2)

            // then
            assertTrue(systemOutRule.log.contains("Could not copy labels because of "))
        }
    }
}
