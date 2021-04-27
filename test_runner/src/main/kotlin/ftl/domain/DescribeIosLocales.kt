package ftl.domain

import flank.common.log
import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.IosArgs
import ftl.environment.getLocaleDescription
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeIosLocales {
    val locale: String
    val configPath: String
}

fun DescribeIosLocales.invoke() {
    if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")
    log(
        fetchLocales(Identity(IosArgs.loadOrDefault(Paths.get(configPath)).project, Platform.IOS)).getLocaleDescription(locale)
    )
}
