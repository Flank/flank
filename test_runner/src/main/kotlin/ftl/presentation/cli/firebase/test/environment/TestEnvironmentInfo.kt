package ftl.presentation.cli.firebase.test.environment

import ftl.api.TestEnvironment
import ftl.environment.android.toCliTable
import ftl.environment.ios.toCliTable
import ftl.presentation.cli.firebase.test.android.orientations.toCliTable
import ftl.presentation.cli.firebase.test.android.versions.toCliTable
import ftl.presentation.cli.firebase.test.ipblocks.toCliTable
import ftl.presentation.cli.firebase.test.locale.toCliTable
import ftl.presentation.cli.firebase.test.networkprofiles.toCliTable
import ftl.presentation.cli.firebase.test.providedsoftware.toCliTable

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
