package ftl

import ftl.config.YamlConfig
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking

object Main {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val config = YamlConfig.load("./flank.yml")
        println(config)

        runBlocking {
            // TestRunner.deleteResults()
            TestRunner.newRun(config)
            // TestRunner.refreshLastRun()
        }
    }
}
