package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.using

/**
 * Parses information from app and test apk files.
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
    TestAndroid.Info(
        packageNames = packageNames,
        testRunners = testRunners
    )
}
