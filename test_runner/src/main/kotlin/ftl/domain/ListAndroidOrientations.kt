package ftl.domain

import flank.common.logLn
import ftl.android.AndroidCatalog
import ftl.args.AndroidArgs
import java.nio.file.Paths

interface ListAndroidOrientations {
    val configPath: String
}

operator fun ListAndroidOrientations.invoke() {
    logLn(AndroidCatalog.supportedOrientationsAsTable(AndroidArgs.loadOrDefault(Paths.get(configPath)).project))
}
