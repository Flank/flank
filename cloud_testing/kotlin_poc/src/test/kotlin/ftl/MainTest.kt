package ftl

import ftl.args.AndroidArgs
import ftl.args.IosArgs
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
        val localConfig = AndroidArgs.load("src/test/kotlin/ftl/fixtures/flank.local.yml")
        val gcsConfig = AndroidArgs.load("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
        runBlocking {
            TestRunner.newRun(localConfig)
            TestRunner.newRun(gcsConfig)
        }
    }

    @Test
    fun mockedIosTestRun() {
        val config = IosArgs.load("src/test/kotlin/ftl/fixtures/flank.ios.yml")
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
