package ftl.gc.android

import com.google.api.services.testing.model.TestSpecification
import ftl.run.platform.android.AndroidTestConfig

internal fun TestSpecification.setupAndroidTest(config: AndroidTestConfig) = apply {
    when (config) {
        is AndroidTestConfig.Instrumentation ->
            androidInstrumentationTest = createAndroidInstrumentationTest(config)
        is AndroidTestConfig.Robo ->
            androidRoboTest = createAndroidRoboTest(config)
        is AndroidTestConfig.GameLoop ->
            androidTestLoop = createGameLoopTest(config)
    }
}
