package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.yml.ResolvedApks
import ftl.util.FlankFatalError

internal fun AndroidArgs.resolveApks() = listOfNotNull(
    element = appApk?.let { appApk ->
        ResolvedApks(
            app = appApk,
            test = testApk,
            additionalApks = additionalApks
        )
    }
).plus(
    elements = additionalAppTestApks.map {
        ResolvedApks(
            app = it.app ?: appApk ?: throw FlankFatalError("Cannot resolve app apk for ${it.test}"),
            test = it.test,
            additionalApks = additionalApks
        )
    }
)
