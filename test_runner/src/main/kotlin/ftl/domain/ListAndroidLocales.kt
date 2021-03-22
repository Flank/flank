package ftl.domain

import flank.common.logLn
import ftl.android.AndroidCatalog
import ftl.args.AndroidArgs
import java.nio.file.Paths

interface ListAndroidLocales {
    val configPath: String
}

operator fun ListAndroidLocales.invoke() {
    logLn(AndroidCatalog.localesAsTable(projectId = AndroidArgs.loadOrDefault(Paths.get(configPath)).project))
}
