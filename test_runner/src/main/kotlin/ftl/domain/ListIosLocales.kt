package ftl.domain

import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.IosArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface ListIosLocales : Output {
    val configPath: String
}

operator fun ListIosLocales.invoke() {
    fetchLocales(Identity(IosArgs.loadOrDefault(Paths.get(configPath)).project, Platform.IOS)).out()
}
