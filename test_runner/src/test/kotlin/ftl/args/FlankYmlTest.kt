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
        val yml = FlankYml(FlankYmlParams(testShards = 1, repeatTests = 1))
        assertThat(yml.flank.repeatTests).isEqualTo(1)
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
    fun testInvalidrepeatTests() {
        expectedExitRule.expectSystemExitWithStatus(-1)
        FlankYml(FlankYmlParams(repeatTests = 0))
    }
}
