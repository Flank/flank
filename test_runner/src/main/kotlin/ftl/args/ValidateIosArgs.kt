package ftl.args

import ftl.ios.IosCatalog
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.IncompatibleTestDimensionError

fun IosArgs.validate() {
    assertXcodeSupported()
    assertDevicesSupported()
    assertTestTypes()
    assertMaxTestShards()
}
private fun IosArgs.assertMaxTestShards() { this.maxTestShards
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
        throw IncompatibleTestDimensionError("iOS ${device.version} on ${device.model} is not a supported device")
}
