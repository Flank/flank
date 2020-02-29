package ftl.test.util

import ftl.mock.MockServer
import org.junit.runners.BlockJUnit4ClassRunner

class FlankTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    companion object {
        init {
            println("FlankTestRunner init\n")

            MockServer.start()
            LocalGcs.uploadFiles()
        }
    }
}
