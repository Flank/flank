package ftl.gc.android

import com.google.api.services.testing.model.AndroidRoboTest
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.RoboDirective
import ftl.config.FlankRoboDirective
import ftl.run.platform.android.AndroidTestConfig

internal fun createAndroidRoboTest(
    config: AndroidTestConfig.Robo
) = AndroidRoboTest().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    roboDirectives = config.flankRoboDirectives?.mapToApiRoboDirectives()
    roboScript = config.roboScriptGcsPath?.let(FileReference()::setGcsPath)
}

private fun List<FlankRoboDirective>.mapToApiRoboDirectives() = map {
    RoboDirective().apply {
        actionType = it.type
        resourceName = it.name
        inputText = it.input
    }
}
