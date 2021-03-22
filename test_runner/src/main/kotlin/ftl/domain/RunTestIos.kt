package ftl.domain

import flank.common.logLn
import ftl.analytics.sendConfiguration
import ftl.args.*
import ftl.config.*
import ftl.mock.MockServer
import ftl.reports.output.configure
import ftl.reports.output.log
import ftl.reports.output.outputReport
import ftl.reports.output.toOutputReportConfiguration
import ftl.run.dumpShards
import ftl.run.newTestRun
import ftl.util.*
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

interface RunIosTest {
    val configPath: String
    val config: IosConfig
    val dryRun: Boolean
    val obfuscate: Boolean
    val dumpShards: Boolean
}

operator fun RunIosTest.invoke() {
    printVersionInfo()

    if (dryRun) {
        MockServer.start()
    }

    createIosArgs(
        config = defaultIosConfig() +
                loadIosConfig(reader = loadFile(Paths.get(configPath))) +
                config,
        obfuscate = obfuscate
    ).apply {
        setupLogLevel()
        outputReport.configure(toOutputReportConfiguration())
        outputReport.log(this)
        setCrashReportTag(
            DEVICE_SYSTEM to "ios",
            TEST_TYPE to type?.name.orEmpty()
        )
        sendConfiguration()
        if (dumpShards.not()) logLn(this)
    }.validate().run {
        if (dumpShards) dumpShards()
        else runBlocking { newTestRun() }
    }
}
