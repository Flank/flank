package ftl.gc.android

import com.google.testing.model.TestSpecification
import ftl.api.TestMatrixAndroid

internal fun TestSpecification.setupAndroidTest(config: TestMatrixAndroid.Type) = apply {
    when (config) {
        is TestMatrixAndroid.Type.Instrumentation -> androidInstrumentationTest = createAndroidInstrumentationTest(config)
        is TestMatrixAndroid.Type.Robo -> androidRoboTest = createAndroidRoboTest(config)
        is TestMatrixAndroid.Type.GameLoop -> androidTestLoop = createGameLoopTest(config)
    }
}
