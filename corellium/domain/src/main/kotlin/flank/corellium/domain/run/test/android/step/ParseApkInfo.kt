package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.ParseApkInfo
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.using

/**
 * The step is parsing information from app and test apk files.
 */
internal val parseApksInfo = ParseApkInfo using context {
    val packageNames = mutableMapOf<String, String>()
    val testRunners = mutableMapOf<String, String>()
    args.apks.map { app ->
        packageNames[app.path] = apk.parsePackageName(app.path)

        app.tests.map { test ->
            val info = apk.parseInfo(test.path)
            packageNames[test.path] = info.packageName
            testRunners[test.path] = info.testRunner
        }
    }
    RunTestCorelliumAndroid.Info(
        packageNames = packageNames,
        testRunners = testRunners
    )
}
