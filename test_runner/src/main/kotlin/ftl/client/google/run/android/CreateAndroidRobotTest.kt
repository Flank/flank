package ftl.client.google.run.android

import com.google.api.services.testing.model.AndroidRoboTest
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.RoboDirective
import ftl.api.TestMatrixAndroid
import ftl.args.FlankRoboDirective

internal fun createAndroidRoboTest(
    config: TestMatrixAndroid.Type.Robo
) = AndroidRoboTest().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    roboDirectives = config.flankRoboDirectives?.mapToApiRoboDirectives()
    roboScript = config.roboScriptGcsPath?.let(FileReference()::setGcsPath)
}

private fun List<FlankRoboDirective>.mapToApiRoboDirectives() = map {
    RoboDirective().apply {
        actionType = actionTypeMap[it.type]
        resourceName = it.name
        inputText = it.input.takeIf(String::isNotBlank)
    }
}

// The Firebase API uses different actionType names than those specified in https://cloud.google.com/sdk/gcloud/reference/beta/firebase/test/android/run#--robo-directives
// https://github.com/bitrise-steplib/steps-virtual-device-testing-for-android/issues/20#issuecomment-384323077
private val actionTypeMap = mapOf(
    "click" to "SINGLE_CLICK",
    "text" to "ENTER_TEXT",
    "ignore" to "IGNORE"
).withDefault {
    "ACTION_TYPE_UNSPECIFIED"
}
