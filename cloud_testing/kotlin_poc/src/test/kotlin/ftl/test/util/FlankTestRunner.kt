package ftl.test.util

import ftl.config.FtlConstants
import org.junit.runners.BlockJUnit4ClassRunner

class FlankTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    companion object {
        init {
            println("FlankTestRunner init")
            MockServer.application.start(wait = false)
            FtlConstants.useMock = true
            TestArtifact.checkFixtures
        }
    }
}
