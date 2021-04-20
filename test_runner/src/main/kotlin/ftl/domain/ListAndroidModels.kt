package ftl.domain

import flank.common.logLn
import ftl.args.AndroidArgs
import ftl.client.google.AndroidCatalog
import java.nio.file.Paths

interface ListAndroidModels {
    val configPath: String
}

operator fun ListAndroidModels.invoke() {
    logLn(AndroidCatalog.devicesCatalogAsTable(AndroidArgs.loadOrDefault(Paths.get(configPath)).project))
}
