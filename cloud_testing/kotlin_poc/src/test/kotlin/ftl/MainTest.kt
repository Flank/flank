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
import java.nio.file.Paths

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
    fun mockedAndroidTestRun() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        val gcsConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.gcs.yml"))
        runBlocking {
            TestRunner.newRun(localConfig)
            TestRunner.newRun(gcsConfig)
        }
    }

    @Test
    fun mockedIosTestRun() {
        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
