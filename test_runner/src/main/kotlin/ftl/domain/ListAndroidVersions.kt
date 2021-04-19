package ftl.domain

import flank.common.logLn
import ftl.args.AndroidArgs
import ftl.client.google.AndroidCatalog
import java.nio.file.Paths

interface ListAndroidVersions {
    val configPath: String
}

operator fun ListAndroidVersions.invoke() {
    logLn(AndroidCatalog.supportedVersionsAsTable(AndroidArgs.loadOrDefault(Paths.get(configPath)).project))
}
