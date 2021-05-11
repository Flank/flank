package ftl.domain

import ftl.api.fetchDeviceModelIos
import ftl.args.IosArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListIosModels : Output {
    val configPath: String
}

operator fun ListIosModels.invoke() {
    fetchDeviceModelIos(IosArgs.loadOrDefault(Paths.get(configPath)).project).out()
}
