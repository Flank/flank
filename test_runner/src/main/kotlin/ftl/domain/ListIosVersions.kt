package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.client.google.IosCatalog
import java.nio.file.Paths

interface ListIosVersions {
    var configPath: String
}

operator fun ListIosVersions.invoke() {
    logLn(IosCatalog.softwareVersionsAsTable(IosArgs.loadOrDefault(Paths.get(configPath)).project))
}
