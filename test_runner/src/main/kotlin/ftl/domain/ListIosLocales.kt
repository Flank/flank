package ftl.domain

import flank.common.logLn
import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.IosArgs
import ftl.presentation.cli.firebase.test.locale.toCliTable
import java.nio.file.Paths

interface ListIosLocales {
    val configPath: String
}

operator fun ListIosLocales.invoke() {
    logLn(fetchLocales(Identity(IosArgs.loadOrDefault(Paths.get(configPath)).project, Platform.IOS)).toCliTable())
}
