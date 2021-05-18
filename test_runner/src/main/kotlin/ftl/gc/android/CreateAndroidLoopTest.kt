package ftl.gc.android

import com.google.testing.model.AndroidTestLoop
import com.google.testing.model.FileReference
import ftl.api.TestMatrixAndroid

internal fun createGameLoopTest(config: TestMatrixAndroid.Type.GameLoop) = AndroidTestLoop().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    scenarioLabels = config.scenarioLabels
    scenarios = config.scenarioNumbers.map { it.toInt() }
}
