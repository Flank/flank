package ftl.test.util

import ftl.config.FtlConstants
import ftl.util.Bash
import org.junit.runners.BlockJUnit4ClassRunner
import java.net.BindException
import java.nio.file.Files
import java.nio.file.Paths

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
            setupResultsDir()
        }

        /** Create one result dir with matrix_ids.json for refresh command tests */
        private fun setupResultsDir() {
            val results = Paths.get("results/2018-09-07_01:21:14.201000_SUfE/matrix_ids.json")
            results.parent.toFile().mkdirs()

            if (results.toFile().exists()) return

            Files.write(
                results, """
                {
                "matrix-1": {
                "matrixId": "matrix-1",
                "state": "FINISHED",
                "gcsPath": "1",
                "webLink": "https://console.firebase.google.com/project/mockProjectId/testlab/histories/1/matrices/1/executions/1",
                "downloaded": false,
                "billableVirtualMinutes": 1,
                "billablePhysicalMinutes": 0,
                "outcome": "success" }
            }
            """.trimIndent().toByteArray()
            )
        }
    }
}
