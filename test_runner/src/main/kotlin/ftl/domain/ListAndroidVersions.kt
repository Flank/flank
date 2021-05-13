package ftl.domain

import flank.common.logLn
import ftl.api.fetchAndroidOsVersion
import ftl.args.AndroidArgs
import ftl.presentation.Output
import ftl.presentation.cli.firebase.test.android.versions.toCliTable
import java.nio.file.Paths

interface ListAndroidVersions : Output {
    val configPath: String
}

operator fun ListAndroidVersions.invoke() {
    // TODO move toCliTable() to presentation layer during refactor of presentation after #1728
    logLn(fetchAndroidOsVersion(AndroidArgs.loadOrDefault(Paths.get(configPath)).project).toCliTable())
}
