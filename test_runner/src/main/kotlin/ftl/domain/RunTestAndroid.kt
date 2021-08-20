package ftl.domain

import flank.common.logLn
import ftl.analytics.reportConfiguration
import ftl.args.createAndroidArgs
import ftl.args.setupLogLevel
import ftl.args.validate
import ftl.config.AndroidConfig
import ftl.config.defaultAndroidConfig
import ftl.config.loadAndroidConfig
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
import ftl.util.DEVICE_SYSTEM
import ftl.util.PROJECT_ID
import ftl.util.StopWatch
import ftl.util.TEST_TYPE
import ftl.util.loadFile
import ftl.util.printVersionInfo
import ftl.util.setCrashReportTag
import java.nio.file.Paths

interface RunTestAndroid : Output {
    val configPath: String
    val config: AndroidConfig
    val dryRun: Boolean
    val obfuscate: Boolean
    val dumpShards: Boolean
}

operator fun RunTestAndroid.invoke() {
    printVersionInfo()

    if (dryRun) {
        MockServer.start()
    }
    val prepareStopWatch = StopWatch().start()
    createAndroidArgs(
        config = defaultAndroidConfig() +
            loadAndroidConfig(reader = loadFile(Paths.get(configPath))) +
            config,
        obfuscate = obfuscate
    ).apply {
        setupLogLevel()

        outputReport.configure(toOutputReportConfiguration())
        outputReport.log(this)
        setCrashReportTag(
            PROJECT_ID to project,
            DEVICE_SYSTEM to "android",
            TEST_TYPE to type?.name.orEmpty()
        )
        reportConfiguration()
    }.validate().also { args ->
        runBlockingWithObservingRunState {
            if (dumpShards)
                args.dumpShards()
            else {
                logLn(args)
                addStepTime("Preparation", prepareStopWatch.check())
                args.newTestRun()
            }
        }
    }
}
