package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.FlankYml
import ftl.args.yml.FlankYmlParams
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class FlankYmlTest {

    @Rule
    @JvmField
    val expectedExitRule: ExpectedSystemExit = ExpectedSystemExit.none()

    @Rule
    @JvmField
    val systemErrRule: SystemErrRule = SystemErrRule().enableLog().muteForSuccessfulTests()

    @Test
    fun testValidArgs() {
        FlankYml()
        FlankYml(FlankYmlParams(testShards = -1))
        val yml = FlankYml(FlankYmlParams(testShards = 1, testRuns = 1))
        assertThat(yml.flank.testRuns).isEqualTo(1)
        assertThat(yml.flank.testShards).isEqualTo(1)
        assertThat(yml.flank.testTargetsAlwaysRun).isEqualTo(emptyList<String>())
        assertThat(FlankYml.map).isNotEmpty()
    }

    @Test
    fun testInvalidTestShards() {
        expectedExitRule.expectSystemExitWithStatus(-1)
        FlankYml(FlankYmlParams(testShards = -2))
    }

    @Test
    fun testInvalidTestRuns() {
        expectedExitRule.expectSystemExitWithStatus(-1)
        FlankYml(FlankYmlParams(testRuns = 0))
    }
}
