package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.yml.UploadedApks
import ftl.gc.GcStorage

internal fun AndroidArgs.createRoboConfig(
    uploadedApks: UploadedApks,
    runGcsPath: String
) = AndroidTestConfig.Robo(
    appApkGcsPath = uploadedApks.app,
    flankRoboDirectives = roboDirectives,
    roboScriptGcsPath = roboScript?.let {
        GcStorage.upload(it, resultsBucket, runGcsPath)
    }
)
