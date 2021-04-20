package ftl.domain

import flank.common.logLn
import ftl.api.Platform
import ftl.api.fetchOrientation
import ftl.args.AndroidArgs
import ftl.environment.common.toCliTable
import java.nio.file.Paths

interface ListAndroidOrientations {
    val configPath: String
}

operator fun ListAndroidOrientations.invoke() {
    logLn(
        fetchOrientation(
            AndroidArgs.loadOrDefault(Paths.get(configPath)).project,
            Platform.ANDROID
        ).toCliTable()
    )
}
