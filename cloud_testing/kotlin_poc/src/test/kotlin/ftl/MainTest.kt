package ftl

import ftl.config.AndroidConfig
import ftl.config.IosConfig
import ftl.run.TestRunner
import ftl.test.util.FlankTestRunner
import ftl.test.util.LocalGcs
import kotlinx.coroutines.experimental.runBlocking
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class MainTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            LocalGcs.setupApks()
        }
    }

    @Test
    fun mockedTestRun() {
        val localConfig = AndroidConfig.load("src/test/kotlin/ftl/fixtures/flank.local.yml")
        val gcsConfig = AndroidConfig.load("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
        runBlocking {
            TestRunner.newRun(localConfig)
            TestRunner.newRun(gcsConfig)
        }
    }

    @Test
    fun mockedIosTestRun() {
        val config = IosConfig.load("src/test/kotlin/ftl/fixtures/flank.ios.yml")
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
