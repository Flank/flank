package ftl.domain

import flank.common.logLn
import ftl.args.createIosArgs
import ftl.args.setupLogLevel
import ftl.args.validate
import ftl.config.IosConfig
import ftl.config.defaultIosConfig
import ftl.config.loadIosConfig
import ftl.config.plus
import ftl.mock.MockServer
import ftl.presentation.Output
import ftl.presentation.runBlockingWithObservingRunState
import ftl.reports.addStepTime
import ftl.reports.output.configure
import ftl.reports.output.log
import ftl.reports.output.outputReport
import ftl.reports.output.toOutputReportConfiguration
import ftl.run.dumpShards
import ftl.run.newTestRun
import ftl.util.StopWatch
import ftl.util.loadFile
import ftl.util.printVersionInfo
import java.nio.file.Paths

interface RunIosTest : Output {
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

    val prepareStopWatch = StopWatch().start()
    createIosArgs(
        config = defaultIosConfig() +
            loadIosConfig(reader = loadFile(Paths.get(configPath))) +
            config,
        obfuscate = obfuscate
    ).apply {
        setupLogLevel()
        outputReport.configure(toOutputReportConfiguration())
        outputReport.log(this)

        if (dumpShards.not()) logLn(this)
    }.validate().run {
        if (dumpShards) dumpShards()
        else runBlockingWithObservingRunState {
            addStepTime("Preparation", prepareStopWatch.check())
            newTestRun()
        }
    }
}
