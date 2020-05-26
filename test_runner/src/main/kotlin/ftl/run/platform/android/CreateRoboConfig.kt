package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.RoboTestContext

internal fun AndroidArgs.createRoboConfig(
    testApk: RoboTestContext
) = AndroidTestConfig.Robo(
    appApkGcsPath = testApk.app.gcs,
    flankRoboDirectives = roboDirectives,
    roboScriptGcsPath = testApk.roboScript.gcs
)
