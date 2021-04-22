package ftl.domain

import flank.common.logLn
import ftl.adapter.google.asPrintableTable
import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.AndroidArgs
import java.nio.file.Paths

interface ListAndroidLocales {
    val configPath: String
}

operator fun ListAndroidLocales.invoke() {
    logLn(
        fetchLocales(Identity(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, Platform.ANDROID)).asPrintableTable()
    )
}
