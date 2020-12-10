package ftl.args

import ftl.args.yml.Type
import ftl.ios.IosCatalog
import ftl.ios.IosCatalog.getSupportedVersionId
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.IncompatibleTestDimensionError

fun IosArgs.validate() = apply {
    commonArgs.validate()
    assertXcodeSupported()
    assertDevicesSupported()
    assertTestTypes()
    assertMaxTestShards()
    assertTestFiles()
    checkResultsDirUnique()
    assertAdditionalIpas()
    validType()
    assertGameloop()
}

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

private fun IosArgs.assertTestTypes() {
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

private fun IosArgs.assertTestFiles() {
    ArgsHelper.assertFileExists(xctestrunFile, "from test")
    ArgsHelper.assertFileExists(xctestrunZip, "from xctestrun-file")
}

private fun IosArgs.assertAdditionalIpas() {
    if (additionalIpas.size > 100) throw FlankConfigurationError("Maximum 100 additional ipas are supported")
}

private fun IosArgs.validType() {
    val validIosTypes = arrayOf(Type.GAMELOOP, Type.XCTEST)
    if (commonArgs.type !in validIosTypes)
        throw FlankConfigurationError("Type should be one of ${validIosTypes.joinToString(",")}")
}
