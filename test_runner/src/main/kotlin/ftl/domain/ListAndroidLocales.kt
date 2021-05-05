package ftl.domain

import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.AndroidArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListAndroidLocales : Output {
    val configPath: String
}

operator fun ListAndroidLocales.invoke() {
    fetchLocales(Identity(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, Platform.ANDROID)).out()
}
