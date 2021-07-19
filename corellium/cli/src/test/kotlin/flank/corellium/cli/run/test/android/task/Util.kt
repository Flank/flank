package flank.corellium.cli.run.test.android.task

import flank.corellium.cli.RunTestCorelliumAndroidCommand
import flank.corellium.domain.RunTestCorelliumAndroid

/**
 * Apply test values to config. Each value should be different than default.
 */
fun RunTestCorelliumAndroidCommand.Config.applyTestValues() = apply {
    auth = "test_auth.yml"
    project = "test project"
    apks = listOf(
        RunTestCorelliumAndroid.Args.Apk.App(
            "app1.apk",
            tests = listOf(
                RunTestCorelliumAndroid.Args.Apk.Test("app1-test1.apk"),
            )
        ),
        RunTestCorelliumAndroid.Args.Apk.App(
            "app2.apk",
            tests = listOf(
                RunTestCorelliumAndroid.Args.Apk.Test("app2-test1.apk"),
                RunTestCorelliumAndroid.Args.Apk.Test("app2-test2.apk"),
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
}
