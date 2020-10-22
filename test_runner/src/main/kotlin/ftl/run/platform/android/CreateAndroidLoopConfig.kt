package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.GameLoopContext

internal fun AndroidArgs.createAndroidLoopConfig(
    testApk: GameLoopContext
) = AndroidTestConfig.GameLoop(
    appApkGcsPath = testApk.app.gcs,
    testApkGcsPath = testApk.test.gcs,
    testRunnerClass = testRunnerClass,
    scenarioLabels = scenarioLabels,
    scenarioNumbers = scenarioNumbers
)
