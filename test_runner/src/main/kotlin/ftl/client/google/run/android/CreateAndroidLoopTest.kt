package ftl.client.google.run.android

import com.google.api.services.testing.model.AndroidTestLoop
import com.google.api.services.testing.model.FileReference
import ftl.api.TestMatrixAndroid

internal fun createGameLoopTest(config: TestMatrixAndroid.Type.GameLoop) = AndroidTestLoop().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    scenarioLabels = config.scenarioLabels
    scenarios = config.scenarioNumbers.map { it.toInt() }
}
