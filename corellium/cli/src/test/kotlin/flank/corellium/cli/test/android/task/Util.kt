package flank.corellium.cli.test.android.task

import flank.corellium.cli.TestAndroidCommand
import flank.corellium.domain.TestAndroid

/**
 * Apply test values to config. Each value should be different than default.
 */
fun TestAndroidCommand.Config.applyTestValues() = apply {
    auth = "test_auth.yml"
    project = "test project"
    apks = listOf(
        TestAndroid.Args.Apk.App(
            "app1.apk",
            tests = listOf(
                TestAndroid.Args.Apk.Test("app1-test1.apk"),
            )
        ),
        TestAndroid.Args.Apk.App(
            "app2.apk",
            tests = listOf(
                TestAndroid.Args.Apk.Test("app2-test1.apk"),
                TestAndroid.Args.Apk.Test("app2-test2.apk"),
            )
        ),
    )
    testTargets = listOf(
        "class foo.Foo",
        "package bar",
    )
    maxTestShards = Int.MAX_VALUE
    localResultsDir = "test_result_dir"
    obfuscate = true
    gpuAcceleration = false
    scanPreviousDurations = 123
    flakyTestAttempts = Int.MAX_VALUE
}
