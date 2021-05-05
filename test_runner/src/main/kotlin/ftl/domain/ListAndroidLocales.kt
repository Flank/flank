package ftl.domain

import flank.common.logLn
import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.AndroidArgs
import ftl.presentation.cli.firebase.test.locale.toCliTable
import java.nio.file.Paths

interface ListAndroidLocales {
    val configPath: String
}

operator fun ListAndroidLocales.invoke() {
    logLn(
        fetchLocales(Identity(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, Platform.ANDROID)).toCliTable()
    )
}
