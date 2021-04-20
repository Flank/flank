package ftl.domain

import flank.common.log
import ftl.args.AndroidArgs
import ftl.client.google.AndroidCatalog
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeAndroidLocales {
    val locale: String
    val configPath: String
}

fun DescribeAndroidLocales.invoke() {
    if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")
    log(AndroidCatalog.getLocaleDescription(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, locale))
}
