package ftl.args

import ftl.android.AndroidCatalog
import ftl.android.IncompatibleModelVersion
import ftl.android.SupportedDeviceConfig
import ftl.android.UnsupportedModelId
import ftl.android.UnsupportedVersionId
import ftl.util.FlankFatalError
import java.io.File

fun AndroidArgs.validate() {
    assertAdditionalAppTestApks()
    assertDevicesSupported()
    assertShards()
    assertTestTypes()
    assertRoboTest()
    assertDirectoriesToPull()
}

private fun AndroidArgs.assertAdditionalAppTestApks() {
    if (appApk == null) additionalAppTestApks
        .filter { (app, _) -> app == null }
        .map { File(it.test).name }
        .run {
            if (isNotEmpty())
                throw FlankFatalError("Cannot resolve app apk pair for $this")
        }
}

private fun AndroidArgs.assertDevicesSupported() = devices
    .associateWith { device ->
        AndroidCatalog.supportedDeviceConfig(device.model, device.version, project)
    }
    .forEach { (device, check) ->
        when (check) {
            SupportedDeviceConfig -> Unit
            UnsupportedModelId -> throw FlankFatalError("Unsupported model id, '${device.model}'\nSupported model ids: ${AndroidCatalog.androidModelIds(project)}")
            UnsupportedVersionId -> throw FlankFatalError("Unsupported version id, '${device.version}'\nSupported Version ids: ${AndroidCatalog.androidVersionIds(project)}")
            IncompatibleModelVersion -> throw FlankFatalError("Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': $check")
        }
    }

private fun AndroidArgs.assertShards() {
    if (numUniformShards != null && maxTestShards > 1) throw FlankFatalError(
        "Option num-uniform-shards cannot be specified along with max-test-shards. Use only one of them."
    )
}

private fun AndroidArgs.assertTestTypes() {
    if (!(isRoboTest or isInstrumentationTest)) throw FlankFatalError(
        "One of following options must be specified [test, robo-directives, robo-script]."
    )
}

private fun AndroidArgs.assertRoboTest() {
    // Using both roboDirectives and roboScript may hang test execution on FTL
    if (roboDirectives.isNotEmpty() && roboScript != null) throw FlankFatalError(
        "Options robo-directives and robo-script are mutually exclusive, use only one of them."
    )
}

// Validation is done according to https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run#--directories-to-pull
private fun AndroidArgs.assertDirectoriesToPull() {
    val correctNameRegex = "(/[a-zA-Z0-9_\\-.+]+)+/?".toRegex()
    directoriesToPull
        .filter { !it.startsWith("/sdcard") && !it.startsWith("/data/local/tmp") || !correctNameRegex.matches(it) }
        .takeIf { it.isNotEmpty() }
        ?.also {
            throw FlankFatalError(
                "Invalid value for [directories-to-pull]: Invalid path $it.\n" +
                "Path must be absolute paths under /sdcard or /data/local/tmp (for example, --directories-to-pull /sdcard/tempDir1,/data/local/tmp/tempDir2).\n" +
                "Path names are restricted to the characters [a-zA-Z0-9_-./+]. "
            )
        }
}
