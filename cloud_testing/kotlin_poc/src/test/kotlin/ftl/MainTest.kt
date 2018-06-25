package ftl

import ftl.config.FtlConstants
import ftl.config.YamlConfig
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class MainTest : TestArtifact() {

    init {
        FtlConstants.useMock = true
    }

    @Test
    fun mockedTestRun() {
        val config = YamlConfig.load("src/test/kotlin/ftl/fixtures/flank.yml")
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Test
    fun mockedIosTestRun() {
        val config = YamlConfig.load("src/test/kotlin/ftl/fixtures/flank.ios.yml")
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
