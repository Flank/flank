package ftl.domain

import flank.common.log
import ftl.adapter.google.getLocaleDescription
import ftl.api.Locale
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.args.AndroidArgs
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeAndroidLocales {
    val locale: String
    val configPath: String
}

fun DescribeAndroidLocales.invoke() {
    if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")
    log(
        fetchLocales(
            Locale.Identity(
                AndroidArgs.loadOrDefault(Paths.get(configPath)).project,
                Platform.ANDROID
            )
        ).getLocaleDescription(locale)
    )
}
