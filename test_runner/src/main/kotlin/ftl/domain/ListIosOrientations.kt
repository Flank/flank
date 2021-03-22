package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.ios.IosCatalog
import java.nio.file.Paths

interface ListIosOrientations {
    var configPath: String
}

operator fun ListIosOrientations.invoke() {
    logLn(IosCatalog.supportedOrientationsAsTable(IosArgs.loadOrDefault(Paths.get(configPath)).project))
}
