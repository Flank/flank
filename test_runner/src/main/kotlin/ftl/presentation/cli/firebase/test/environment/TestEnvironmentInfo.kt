package ftl.presentation.cli.firebase.test.environment

import ftl.api.TestEnvironment
import ftl.environment.android.toCliTable
import ftl.environment.common.toCliTable
import ftl.environment.ios.toCliTable
import ftl.environment.toCliTable

fun TestEnvironment.Android.prepareOutputString() = buildString {
    appendLine(osVersions.toCliTable())
    appendLine(models.toCliTable())
    appendLine(locales.toCliTable())
    appendLine(softwareCatalog.toCliTable())
    appendLine(networkProfiles.toCliTable())
    appendLine(orientations.toCliTable())
    appendLine(ipBlocks.toCliTable())
}

fun TestEnvironment.Ios.prepareOutputString() = buildString {
    appendLine(osVersions.toCliTable())
    appendLine(models.toCliTable())
    appendLine(locales.toCliTable())
    appendLine(softwareCatalog.toCliTable())
    appendLine(networkProfiles.toCliTable())
    appendLine(orientations.toCliTable())
    appendLine(ipBlocks.toCliTable())
}
