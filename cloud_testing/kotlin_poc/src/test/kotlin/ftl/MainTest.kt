package ftl

import ftl.config.FtlConstants
import ftl.config.YamlConfig
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class MainTest {

    init {
        FtlConstants.useMock = true
    }

    @Test
    fun mockedTestRun() {
        val config = YamlConfig.load("./flank.yml")
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
