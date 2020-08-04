package ftl.args

import ftl.ios.IosCatalog
import ftl.util.FlankFatalError
import ftl.util.IncompatibleTestDimension

fun IosArgs.validate() {
    assertXcodeSupported()
    assertDevicesSupported()
    assertMaxTestShards()
}
private fun IosArgs.assertMaxTestShards() { this.maxTestShards
    if (
        maxTestShards !in IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE &&
        maxTestShards != -1
    ) throw FlankFatalError(
        "max-test-shards must be >= ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.first} and <= ${IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}, or -1. But current is $maxTestShards"
    )
}
private fun IosArgs.assertXcodeSupported() = when {
    xcodeVersion == null -> Unit
    IosCatalog.supportedXcode(xcodeVersion, project) -> Unit
    else -> throw IncompatibleTestDimension(("Xcode $xcodeVersion is not a supported Xcode version"))
}

private fun IosArgs.assertDevicesSupported() = devices.forEach { device ->
    if (!IosCatalog.supportedDevice(device.model, device.version, this.project))
        throw IncompatibleTestDimension("iOS ${device.version} on ${device.model} is not a supported device")
}
