package ftl.domain

import ftl.api.Platform
import ftl.api.fetchOrientation
import ftl.args.IosArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListIosOrientations : Output {
    var configPath: String
}

operator fun ListIosOrientations.invoke() {
    fetchOrientation(
        IosArgs.loadOrDefault(Paths.get(configPath)).project,
        Platform.IOS
    ).out()
}
