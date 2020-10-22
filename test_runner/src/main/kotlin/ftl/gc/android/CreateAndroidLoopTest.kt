package ftl.gc.android

import com.google.api.services.testing.model.AndroidTestLoop
import com.google.api.services.testing.model.FileReference
import ftl.run.platform.android.AndroidTestConfig

internal fun createGameLoopTest(config: AndroidTestConfig.GameLoop) = AndroidTestLoop().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    scenarioLabels = config.scenarioLabels
    scenarios = config.scenarioNumbers.map { it.toInt() }
}
