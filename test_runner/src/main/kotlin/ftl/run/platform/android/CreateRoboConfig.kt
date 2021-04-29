package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.RoboTestContext
import ftl.run.model.SanityRoboTestContext

internal fun AndroidArgs.createRoboConfig(
    testApk: RoboTestContext
) = AndroidTestConfig.Robo(
    appApkGcsPath = testApk.app.remote,
    flankRoboDirectives = roboDirectives,
    roboScriptGcsPath = testApk.roboScript.remote
)

internal fun createSanityRoboConfig(
    testApk: SanityRoboTestContext
) = AndroidTestConfig.Robo(
    appApkGcsPath = testApk.app.remote,
    flankRoboDirectives = null,
    roboScriptGcsPath = null
)
