package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.test.util.FlankTestRunner
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class TestRunnerTest {

    @Test
    fun mockedAndroidTestRun_local() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        runBlocking {
            TestRunner.newRun(localConfig)
        }
    }

    @Test
    fun mockedAndroidTestRun_gcsAndHistoryName() {
        val gcsConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.gcs.yml"))
        runBlocking {
            TestRunner.newRun(gcsConfig)
        }
    }

    @Test
    fun mockedIosTestRun_local() {
        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Test
    fun mockedIosTestRun_gcsAndHistoryName() {
        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.gcs.yml"))
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
