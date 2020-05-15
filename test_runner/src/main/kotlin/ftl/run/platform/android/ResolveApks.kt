package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.yml.ResolvedApks

internal fun AndroidArgs.resolveApks() = listOf(
    element = ResolvedApks(
        app = appApk,
        test = testApk,
        additionalApks = additionalApks
    )
).plus(
    elements = additionalAppTestApks.map {
        ResolvedApks(
            app = it.app ?: appApk,
            test = it.test,
            additionalApks = additionalApks
        )
    }
)
