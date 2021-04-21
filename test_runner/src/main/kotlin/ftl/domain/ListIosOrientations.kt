package ftl.domain

import flank.common.logLn
import ftl.api.Platform
import ftl.api.fetchOrientation
import ftl.args.IosArgs
import ftl.environment.common.toCliTable
import java.nio.file.Paths

interface ListIosOrientations {
    var configPath: String
}

operator fun ListIosOrientations.invoke() {
    // TODO move toCliTable() to presentation layer during refactor of presentation after #1728
    logLn(
        fetchOrientation(
            IosArgs.loadOrDefault(Paths.get(configPath)).project,
            Platform.IOS
        ).toCliTable()
    )
}
