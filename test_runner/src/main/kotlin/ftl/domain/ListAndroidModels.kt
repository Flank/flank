package ftl.domain

import ftl.api.fetchDeviceModelAndroid
import ftl.args.AndroidArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListAndroidModels : Output {
    val configPath: String
}

operator fun ListAndroidModels.invoke() {
    fetchDeviceModelAndroid(AndroidArgs.loadOrDefault(Paths.get(configPath)).project).out()
}
