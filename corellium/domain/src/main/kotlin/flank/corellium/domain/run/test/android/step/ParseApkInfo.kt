package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.ParseApkInfo
import flank.corellium.domain.step

/**
 * The step is parsing information from app and test apk files.
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.packageNames]
 * * [RunTestCorelliumAndroid.State.testRunners]
 */
internal fun RunTestCorelliumAndroid.Context.parseApksInfo() = step(ParseApkInfo) {
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
    copy(
        packageNames = packageNames,
        testRunners = testRunners
    )
}
