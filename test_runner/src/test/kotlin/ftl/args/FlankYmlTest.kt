package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.FlankYml
import ftl.args.yml.FlankYmlParams
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class FlankYmlTest {

    @Rule
    @JvmField
    val exceptionRule = ExpectedException.none()!!

    @Rule
    @JvmField
    val systemErrRule: SystemErrRule = SystemErrRule().enableLog().muteForSuccessfulTests()

    @Test
    fun testValidArgs() {
        FlankYml()
        FlankYml(FlankYmlParams(maxTestShards = -1))
        val yml = FlankYml(FlankYmlParams(maxTestShards = 1, repeatTests = 1, shardTime = 58))
        assertThat(yml.flank.repeatTests).isEqualTo(1)
        assertThat(yml.flank.maxTestShards).isEqualTo(1)
        assertThat(yml.flank.shardTime).isEqualTo(58)
        assertThat(yml.flank.testTargetsAlwaysRun).isEqualTo(emptyList<String>())
        assertThat(yml.flank.runTimeout).isEqualTo("-1")
        assertThat(FlankYml.map).isNotEmpty()
    }
}
