package ftl.domain

import flank.common.logLn
import ftl.api.fetchDeviceModelIos
import ftl.args.IosArgs
import ftl.environment.ios.toCliTable
import java.nio.file.Paths

interface ListIosModels {
    val configPath: String
}

operator fun ListIosModels.invoke() {
    // TODO move toCliTable() and printing presentation layer during refactor of presentation after #1728
    logLn(fetchDeviceModelIos(IosArgs.loadOrDefault(Paths.get(configPath)).project).toCliTable())
}
