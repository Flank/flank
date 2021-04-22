package ftl.args

import com.google.common.annotations.VisibleForTesting
import flank.common.logLn
import ftl.api.fetchDeviceModelAndroid
import ftl.args.yml.Type
import ftl.client.google.AndroidCatalog
import ftl.client.google.DeviceConfigCheck
import ftl.client.google.IncompatibleModelVersion
import ftl.client.google.SupportedDeviceConfig
import ftl.client.google.UnsupportedModelId
import ftl.client.google.UnsupportedVersionId
import ftl.config.Device
import ftl.config.containsPhysicalDevices
import ftl.config.containsVirtualDevices
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.IncompatibleTestDimensionError
import java.io.File

fun AndroidArgs.validate() = if (shouldValidateConfig) apply {
    commonArgs.validate()
    assertDevicesSupported()
    assertShards()
    assertTestTypes()
    assertDirectoriesToPull()
    assertMaxTestShardsByDeviceType()
    assertParametersConflict()
    assertTestFiles()
    assertTestTargetForShards()
    assertOtherFiles()
    assertGrantPermissions()
    assertType()
    assertGameLoop()
    checkResultsDirUnique()
    checkEnvironmentVariables()
    checkFilesToDownload()
    checkNumUniformShards()
} else this

private fun AndroidArgs.assertTestTargetForShards() {
    if (testTargetsForShard.isNotEmpty()) {
        when {
            numUniformShards != null -> throw FlankConfigurationError("Number Of Uniform Shards and test Targets For Shards cannot be defined at the same time.")
            isInstrumentationTest.not() -> throw FlankConfigurationError("Test target for shards can only be specified when test type is 'instrumentation'.")
            devices.isEmpty() && testTargetsForShard.size > 500 -> throw FlankConfigurationError("Cannot have more than 500 Test Targets for Shards with no device provided.")
            devices.isNotEmpty() && testTargetsForShard.size > 50 -> throw FlankConfigurationError("Cannot have more than 50 Test Targets for Shards if one or more devices are specified.")
        }
    }
}

private fun AndroidArgs.assertGameLoop() {
    assertLabelContent()
    assertObbFiles()
}

private fun AndroidArgs.assertObbFiles() {
    when {
        obbFiles.isEmpty() && obbNames.isEmpty() -> Unit
        (obbFiles.size != obbNames.size) ->
            throw FlankConfigurationError("Obb files and Obb File names provided are invalid. Amount provided does not match - Obb files (${obbFiles.size}) vs Obb file names (${obbNames.size})")
    }
}

private fun AndroidArgs.assertLabelContent() {
    if (scenarioLabels.isNotEmpty() && (type == null || type != Type.GAMELOOP))
        throw FlankConfigurationError("Scenario labels defined but Type is not Game-loop. ($type)")

    when {
        obbFiles.isEmpty() -> Unit
        (type == null || type != Type.GAMELOOP) -> throw FlankConfigurationError("OBB files defined but Type is not Game-loop. ($type)")
        obbFiles.size > MAX_OBB_FILES -> throw FlankConfigurationError("Up to two OBB files are supported. Currently ${obbFiles.size} OBB files have been supplied.")
        else -> obbFiles.forEach { ArgsHelper.assertFileExists(it, " (obb file)") }
    }

    if (scenarioNumbers.isNotEmpty() && (type != Type.GAMELOOP))
        throw FlankConfigurationError("Scenario numbers defined but Type is not Game-loop.")
    scenarioNumbers.forEach {
        it.toIntOrNull() ?: throw FlankConfigurationError("Invalid scenario number provided - $it")
    }
    if (scenarioNumbers.size > 1024) throw FlankConfigurationError("There cannot be more than 1024 Scenario numbers")
}

private const val MAX_OBB_FILES = 2

private fun AndroidArgs.assertType() = type?.let {
    if (appApk == null) throw FlankGeneralError("A valid AppApk must be defined if Type parameter is used.")
    if (it == Type.INSTRUMENTATION) {
        if (testApk == null) throw FlankGeneralError("Instrumentation tests require a valid testApk defined.")
    }
}

private fun AndroidArgs.assertGrantPermissions() = grantPermissions?.let {
    if (it !in listOf("all", "none")
    ) throw FlankGeneralError("Unsupported permission '$grantPermissions'\nOnly 'all' or 'none' supported.")
}

private fun AndroidArgs.assertDevicesSupported() = devices
    .associateWith { device ->
        supportedDeviceConfig(device.model, device.version, project)
    }
    .forEach { (device, check) ->
        when (check) {
            SupportedDeviceConfig -> Unit
            UnsupportedModelId -> throw IncompatibleTestDimensionError(
                "Unsupported model id, '${device.model}'\nSupported model ids: ${
                AndroidCatalog.androidModelIds(project)
                }"
            )
            UnsupportedVersionId -> throw IncompatibleTestDimensionError(
                "Unsupported version id, '${device.version}'\nSupported Version ids: ${
                AndroidCatalog.androidVersionIds(project)
                }"
            )
            IncompatibleModelVersion -> throw IncompatibleTestDimensionError(
                "Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': ${
                device.getSupportedVersionId(project).joinToString { it }
                }"
            )
        }
    }

@VisibleForTesting
internal fun supportedDeviceConfig(modelId: String, versionId: String, projectId: String): DeviceConfigCheck {
    val foundModel = fetchDeviceModelAndroid(projectId).find { it.id == modelId } ?: return UnsupportedModelId
    if (!AndroidCatalog.androidVersionIds(projectId).contains(versionId)) return UnsupportedVersionId
    if (!foundModel.supportedVersionIds.contains(versionId)) return IncompatibleModelVersion

    return SupportedDeviceConfig
}

fun Device.getSupportedVersionId(
    projectId: String
): List<String> = fetchDeviceModelAndroid(projectId).find { it.id == model }?.supportedVersionIds.orEmpty()

private fun AndroidArgs.assertShards() {
    if (numUniformShards != null && maxTestShards > 1) throw FlankConfigurationError(
        "Option num-uniform-shards cannot be specified along with max-test-shards. Use only one of them."
    )
}

private fun AndroidArgs.assertTestTypes() {
    if (!(isRoboTest or isInstrumentationTest or isSanityRobo or isGameLoop)) throw FlankConfigurationError("Unable to infer test type. Please check configuration")
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
    if (inVirtualRange && !inPhysicalRange) logLn("Physical devices configured, but max-test-shards limit set to $maxTestShards, for physical devices range is ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.first} to ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}, you additionally have configured virtual devices. In this case, the physical limit will be decreased to: ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}")
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
    ).filterNotNull() + additionalAppTestApks.fold(emptyMap()) { acc, pair ->
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

private fun AndroidArgs.checkEnvironmentVariables() {
    if (environmentVariables.isNotEmpty() && directoriesToPull.isEmpty())
        logLn("WARNING: environment-variables set but directories-to-pull is empty, this will result in the coverage file  not downloading to the bucket.")
}

private fun AndroidArgs.checkFilesToDownload() {
    if (filesToDownload.isNotEmpty() && directoriesToPull.isEmpty())
        logLn("WARNING: files-to-download is set but directories-to-pull is empty, the coverage file may fail to download into the bucket.")
}

private fun AndroidArgs.checkNumUniformShards() {
    if ((numUniformShards ?: 0) > 0 && disableSharding)
        logLn("WARNING: disable-sharding is enabled with num-uniform-shards = $numUniformShards, Flank will ignore num-uniform-shards and disable sharding.")
}
