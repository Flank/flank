package ftl

import ftl.android.LocalGcs
import ftl.config.FtlConstants
import ftl.config.YamlConfig
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import org.junit.BeforeClass
import org.junit.Test

class MainTest : TestArtifact() {

    @Test
    fun mockedTestRun() {
        val localConfig = YamlConfig.load("src/test/kotlin/ftl/fixtures/flank.local.yml")
        val gcsConfig = YamlConfig.load("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
        runBlocking {
            TestRunner.newRun(localConfig)
            TestRunner.newRun(gcsConfig)
        }
    }

    @Test
    fun mockedIosTestRun() {
        val config = YamlConfig.load("src/test/kotlin/ftl/fixtures/flank.ios.yml")
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            FtlConstants.useMock = true
            LocalGcs.setupApks()
        }
    }
}
