package ftl.domain

import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.IosArgs
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeIosLocales : Output {
    val locale: String
    val configPath: String
}

fun DescribeIosLocales.invoke() {
    if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")

    fetchLocales(Identity(IosArgs.loadOrDefault(Paths.get(configPath)).project, Platform.IOS, locale)).out()
}
