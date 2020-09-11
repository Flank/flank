package ftl.args

import ftl.android.AndroidCatalog
import ftl.android.IncompatibleModelVersion
import ftl.android.SupportedDeviceConfig
import ftl.android.UnsupportedModelId
import ftl.android.UnsupportedVersionId
import ftl.config.containsPhysicalDevices
import ftl.config.containsVirtualDevices
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.IncompatibleTestDimensionError
import java.io.File

fun AndroidArgs.validate() = apply {
    commonArgs.validate()
    assertDevicesSupported()
    assertShards()
    assertTestTypes()
    assertDirectoriesToPull()
    assertMaxTestShardsByDeviceType()
    assertParametersConflict()
    assertTestFiles()
    assertOtherFiles()
    checkResultsDirUnique()
}

private fun AndroidArgs.assertDevicesSupported() = devices
    .associateWith { device ->
        AndroidCatalog.supportedDeviceConfig(device.model, device.version, project)
    }
    .forEach { (device, check) ->
        when (check) {
            SupportedDeviceConfig -> Unit
            UnsupportedModelId -> throw IncompatibleTestDimensionError("Unsupported model id, '${device.model}'\nSupported model ids: ${AndroidCatalog.androidModelIds(project)}")
            UnsupportedVersionId -> throw IncompatibleTestDimensionError("Unsupported version id, '${device.version}'\nSupported Version ids: ${AndroidCatalog.androidVersionIds(project)}")
            IncompatibleModelVersion -> throw IncompatibleTestDimensionError("Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': $check")
        }
    }

private fun AndroidArgs.assertShards() {
    if (numUniformShards != null && maxTestShards > 1) throw FlankConfigurationError(
        "Option num-uniform-shards cannot be specified along with max-test-shards. Use only one of them."
    )
}

private fun AndroidArgs.assertTestTypes() {
    if (!(isRoboTest or isInstrumentationTest or isSanityRobo)) throw FlankConfigurationError("Unable to infer test type. Please check configuration")
}

// Validation is done according to https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run#--directories-to-pull
private fun AndroidArgs.assertDirectoriesToPull() {
    val correctNameRegex = "(/[a-zA-Z0-9_\\-.+]+)+/?".toRegex()
    directoriesToPull
        .filter { !it.startsWith("/sdcard") && !it.startsWith("/data/local/tmp") || !correctNameRegex.matches(it) }
        .takeIf { it.isNotEmpty() }
        ?.also {
            throw FlankConfigurationError(
                "Invalid value for [directories-to-pull]: Invalid path $it.\n" +
                    "Path must be absolute paths under /sdcard or /data/local/tmp (for example, --directories-to-pull /sdcard/tempDir1,/data/local/tmp/tempDir2).\n" +
                    "Path names are restricted to the characters [a-zA-Z0-9_-./+]. "
            )
        }
}

private fun AndroidArgs.assertMaxTestShardsByDeviceType() =
    when {
        devices.containsPhysicalDevices() && devices.containsVirtualDevices() -> assertDevicesShards()
        devices.containsPhysicalDevices() && !devices.containsVirtualDevices() && !inPhysicalRange -> throwMaxTestShardsLimitExceeded()
        else -> assertVirtualDevicesShards()
    }

private fun AndroidArgs.assertDevicesShards() {
    if (inVirtualRange && !inPhysicalRange) println("Physical devices configured, but max-test-shards limit set to $maxTestShards, for physical devices range is ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.first} to ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}, you additionally have configured virtual devices. In this case, the physical limit will be decreased to: ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}")
    else if (!inVirtualRange && !inPhysicalRange) throwMaxTestShardsLimitExceeded()
}

private fun AndroidArgs.assertVirtualDevicesShards() {
    if (!inVirtualRange) throwMaxTestShardsLimitExceeded()
}

private fun AndroidArgs.throwMaxTestShardsLimitExceeded(): Nothing {
    throw FlankConfigurationError(
        "max-test-shards must be >= ${IArgs.AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.first} and <= ${IArgs.AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last} for virtual devices, for physical devices max-test-shards must be >= ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.first} and <= ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}, or -1. But current is $maxTestShards"
    )
}

private fun AndroidArgs.assertParametersConflict() {
    if (useLegacyJUnitResult && fullJUnitResult)
        throw FlankConfigurationError("Parameters conflict, you cannot set: `--legacy-junit-result` and `--full-junit-result` at the same time.")
}

private fun AndroidArgs.assertTestFiles() {
    if (isInstrumentationTest) assertInstrumentationTest()
    if (isRoboTest) assertRoboTest()
}

private fun AndroidArgs.assertInstrumentationTest() {
    assertAdditionalAppTestApks()
    assertApkFilePaths()
}

private fun AndroidArgs.assertAdditionalAppTestApks() {
    if (appApk == null) additionalAppTestApks
        .filter { (app, _) -> app == null }
        .map { File(it.test).name }
        .run { if (isNotEmpty()) throw FlankConfigurationError("Cannot resolve app apk pair for $this") }
}

private fun AndroidArgs.assertApkFilePaths() {
    appApkPath().forEach { (file, comment) ->
        ArgsHelper.assertFileExists(file, comment)
    }
}

private fun AndroidArgs.appApkPath(): Map<String, String> =
    mapOf(
        appApk to "from app",
        testApk to "from test"
    ).filterNotNull() + additionalAppTestApks.fold(emptyMap<String, String>()) { acc, pair ->
        acc + mapOf(
            pair.app to "from additional-app-test-apks.app",
            pair.test to "from additional-app-test-apks.test"
        ).filterNotNull()
    }

private fun Map<String?, String>.filterNotNull() = filter { it.key != null }.mapKeys { it.key!! }

private fun AndroidArgs.assertRoboTest() {
    // Using both roboDirectives and roboScript may hang test execution on FTL
    if (roboDirectives.isNotEmpty() && roboScript != null) throw FlankConfigurationError(
        "Options robo-directives and robo-script are mutually exclusive, use only one of them."
    )
    if (roboScript != null)
        ArgsHelper.assertFileExists(roboScript.toString(), "from roboScript")
    ArgsHelper.assertFileExists(appApk.toString(), "from app")
}

private fun AndroidArgs.assertOtherFiles() {
    otherFiles.forEach { (_, path) -> ArgsHelper.assertFileExists(path, "from otherFiles") }
}
