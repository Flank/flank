package ftl.domain

import flank.common.logLn
import ftl.analytics.FLANK_VERSION
import ftl.analytics.FLANK_VERSION_PROPERTY
import ftl.analytics.sendConfiguration
import ftl.args.createAndroidArgs
import ftl.args.setupLogLevel
import ftl.args.validate
import ftl.config.AndroidConfig
import ftl.config.defaultAndroidConfig
import ftl.config.loadAndroidConfig
import ftl.config.plus
import ftl.mock.MockServer
import ftl.reports.addStepTime
import ftl.reports.output.configure
import ftl.reports.output.log
import ftl.reports.output.outputReport
import ftl.reports.output.toOutputReportConfiguration
import ftl.run.dumpShards
import ftl.run.newTestRun
import ftl.util.DEVICE_SYSTEM
import ftl.util.StopWatch
import ftl.util.TEST_TYPE
import ftl.util.loadFile
import ftl.util.printVersionInfo
import ftl.util.readVersion
import ftl.util.setCrashReportTag
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

interface RunTestAndroid {
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
            DEVICE_SYSTEM to "android",
            TEST_TYPE to type?.name.orEmpty()
        )
        sendConfiguration()
        sendConfiguration(mapOf(FLANK_VERSION_PROPERTY to readVersion()), eventName = FLANK_VERSION)
    }.validate().also { args ->
        runBlocking {
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
