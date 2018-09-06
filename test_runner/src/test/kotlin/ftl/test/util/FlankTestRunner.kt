package ftl.test.util

import ftl.config.FtlConstants
import ftl.util.Bash
import org.junit.runners.BlockJUnit4ClassRunner
import java.net.BindException

class FlankTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    companion object {
        init {
            println("FlankTestRunner init\n")
            val server = MockServer.application

            try {
                server.start(wait = false)
            } catch (e: BindException) {
                val lsofOutput = Bash.execute("lsof -i :${MockServer.port}")
                val pid = lsofOutput.split("\n").last().split(Regex("\\s+"))[1]
                Bash.execute("kill -9 $pid")
                Thread.sleep(2000)
                server.start(wait = false)
            }
            FtlConstants.useMock = true
            TestArtifact.checkFixtures
            LocalGcs.setupApks()
        }
    }
}
