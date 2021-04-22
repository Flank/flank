package ftl.domain

import flank.common.log
import ftl.args.IosArgs
import ftl.client.google.IosCatalog
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeIosLocales {
    val locale: String
    val configPath: String
}

fun DescribeIosLocales.invoke() {
    if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")
    log(IosCatalog.getLocaleDescription(IosArgs.loadOrDefault(Paths.get(configPath)).project, locale))
}
