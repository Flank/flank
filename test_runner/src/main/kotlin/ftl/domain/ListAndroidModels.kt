package ftl.domain

import flank.common.logLn
import ftl.api.fetchDeviceModelAndroid
import ftl.args.AndroidArgs
import ftl.environment.android.toCliTable
import java.nio.file.Paths

interface ListAndroidModels {
    val configPath: String
}

operator fun ListAndroidModels.invoke() {
    // TODO move toCliTable() and printing presentation layer during refactor of presentation after #1728
    logLn(fetchDeviceModelAndroid(AndroidArgs.loadOrDefault(Paths.get(configPath)).project).toCliTable())
}
