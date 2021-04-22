package ftl.args

import flank.common.logLn
import ftl.args.yml.Type
import ftl.client.google.IosCatalog
import ftl.client.google.IosCatalog.getSupportedVersionId
import ftl.ios.xctest.XcTestRunData
import ftl.ios.xctest.common.XcTestRunVersion
import ftl.ios.xctest.common.mapToRegex
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.IncompatibleTestDimensionError

fun IosArgs.validate() = if (shouldValidateConfig) apply {
    commonArgs.validate()
    assertXcodeSupported()
    assertDevicesSupported()
    assertTestTypes()
    assertMaxTestShards()
    assertTestFiles()
    checkResultsDirUnique()
    assertAdditionalIpas()
    validType()
    assertXcTestRunVersion()
    assertGameloop()
    assertXcTestRunData()
} else this

private fun IosArgs.assertGameloop() {
    validateApp()
    validateScenarioNumbers()
}

private fun IosArgs.validateApp() {
    if (app.isNotEmpty() && type != Type.GAMELOOP) throw FlankConfigurationError("App cannot be defined if type is not equal to game-loop (IOS)")
}

private fun IosArgs.validateScenarioNumbers() {
    if (scenarioNumbers.isNotEmpty() && (type != Type.GAMELOOP))
        throw FlankConfigurationError("Scenario numbers defined but Type is not Game-loop.")
    scenarioNumbers.forEach {
        it.toIntOrNull() ?: throw FlankConfigurationError("Invalid scenario number provided - $it")
    }
    if (scenarioNumbers.size > 1024) throw FlankConfigurationError("There cannot be more than 1024 Scenario numbers")
}

fun IosArgs.validateRefresh() = apply {
    commonArgs.validate()
    assertXcodeSupported()
    assertDevicesSupported()
    assertTestTypes()
    assertMaxTestShards()
}

private fun IosArgs.assertMaxTestShards() {
    if (
        maxTestShards !in IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE &&
        maxTestShards != -1
    ) throw FlankConfigurationError(
        "max-test-shards must be >= ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.first} and <= ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}, or -1. But current is $maxTestShards"
    )
}

private fun IosArgs.assertTestTypes() =
    if (type == Type.GAMELOOP) validateGameloopFiles()
    else validateXcTestTypes()

private fun IosArgs.validateGameloopFiles() {
    if (app.isBlank()) throw FlankConfigurationError("When [gameloop] is specified, [app] must be set.")
}

private fun IosArgs.validateXcTestTypes() {
    if (xctestrunFile.isBlank() or xctestrunZip.isBlank())
        throw FlankConfigurationError("Both of following options must be specified [test, xctestrun-file].")
}

private fun IosArgs.assertXcodeSupported() = when {
    xcodeVersion == null -> Unit
    IosCatalog.supportedXcode(xcodeVersion, project) -> Unit
    else -> throw IncompatibleTestDimensionError(("Xcode $xcodeVersion is not a supported Xcode version"))
}

private fun IosArgs.assertDevicesSupported() = devices.forEach { device ->
    if (!IosCatalog.supportedDevice(device.model, device.version, this.project))
        throw IncompatibleTestDimensionError("iOS ${device.version} on ${device.model} is not a supported\nSupported version ids for '${device.model}': ${device.getSupportedVersionId(project).joinToString()}")
}

private fun IosArgs.assertTestFiles() =
    if (isXcTest) {
        ArgsHelper.assertFileExists(xctestrunFile, "from test")
        ArgsHelper.assertFileExists(xctestrunZip, "from xctestrun-file")
    } else ArgsHelper.assertFileExists(app, "from app")

private fun IosArgs.assertAdditionalIpas() {
    if (additionalIpas.size > 100) throw FlankConfigurationError("Maximum 100 additional ipas are supported")
}

private fun IosArgs.validType() {
    val validIosTypes = arrayOf(Type.GAMELOOP, Type.XCTEST)
    if (commonArgs.type !in validIosTypes)
        throw FlankConfigurationError("Type should be one of ${validIosTypes.joinToString(",")}")
}

private fun IosArgs.assertXcTestRunVersion() {
    if (filterTestConfiguration && xcTestRunData.version == XcTestRunVersion.V1)
        throw FlankConfigurationError("Specified [xctestrun-file] doesn't contain test plans. Options: [only-test-configuration] or [skip-test-configuration] are not valid for this [xctestrun-file]")
}

private val IosArgs.filterTestConfiguration
    get() = onlyTestConfiguration.isNotBlank() or skipTestConfiguration.isNotBlank()

private fun IosArgs.assertXcTestRunData() =
    takeIf { isXcTest && !disableSharding && testTargets.isNotEmpty() }
        ?.let {
            val filteredMethods = xcTestRunData.filterMethods()

            if (filteredMethods.isEmpty()) throw FlankGeneralError(
                "Empty shards. Cannot match any method to $testTargets"
            )

            if (filteredMethods.size < testTargets.size) {
                val regexList = testTargets.mapToRegex()

                val notMatched = testTargets.filter {
                    filteredMethods.all { method ->
                        regexList.any { regex ->
                            regex.matches(method)
                        }
                    }
                }

                logLn("WARNING: cannot match test_targets: $notMatched")
            }
        }

private fun XcTestRunData.filterMethods() = shardTargets.values
    .flatten()
    .flatMap { it.values }
    .flatten()
