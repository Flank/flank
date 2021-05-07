package ftl.domain

import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.AndroidArgs
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

interface DescribeAndroidLocales : Output {
    val locale: String
    val configPath: String
}

fun DescribeAndroidLocales.invoke() {
    if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")

    fetchLocales(Identity(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, Platform.ANDROID)).find {
        it.id == locale
    }?.out() ?: throw FlankGeneralError("ERROR: '$locale' is not a valid locale")
}
