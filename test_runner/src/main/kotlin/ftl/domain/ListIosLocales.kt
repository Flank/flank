package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.client.google.IosCatalog
import java.nio.file.Paths

interface ListIosLocales {
    val configPath: String
}

operator fun ListIosLocales.invoke() {
    logLn(IosCatalog.localesAsTable(projectId = IosArgs.loadOrDefault(Paths.get(configPath)).project))
}
