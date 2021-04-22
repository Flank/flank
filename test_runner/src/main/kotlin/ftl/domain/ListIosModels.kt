package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.client.google.IosCatalog
import java.nio.file.Paths

interface ListIosModels {
    val configPath: String
}

operator fun ListIosModels.invoke() {
    logLn(IosCatalog.devicesCatalogAsTable(IosArgs.loadOrDefault(Paths.get(configPath)).project))
}
