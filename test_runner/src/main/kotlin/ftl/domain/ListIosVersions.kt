package ftl.domain

import ftl.api.fetchIosOsVersion
import ftl.args.IosArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListIosVersions : Output {
    var configPath: String
}

operator fun ListIosVersions.invoke() {
    fetchIosOsVersion(IosArgs.loadOrDefault(Paths.get(configPath)).project).out()
}
