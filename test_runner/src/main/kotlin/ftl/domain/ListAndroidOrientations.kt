package ftl.domain

import ftl.api.Platform
import ftl.api.fetchOrientation
import ftl.args.AndroidArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListAndroidOrientations : Output {
    val configPath: String
}

operator fun ListAndroidOrientations.invoke() {
    fetchOrientation(
        AndroidArgs.loadOrDefault(Paths.get(configPath)).project,
        Platform.ANDROID
    ).out()
}
