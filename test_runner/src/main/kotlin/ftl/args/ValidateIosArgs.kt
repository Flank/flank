package ftl.args

import ftl.ios.IosCatalog
import ftl.util.FlankFatalError

fun IosArgs.validate() {
    assertXcodeSupported()
    assertDevicesSupported()
}

private fun IosArgs.assertXcodeSupported() = when {
    xcodeVersion == null -> Unit
    IosCatalog.supportedXcode(xcodeVersion, project) -> Unit
    else -> throw FlankFatalError(("Xcode $xcodeVersion is not a supported Xcode version"))
}

private fun IosArgs.assertDevicesSupported() = devices.forEach { device ->
    if (!IosCatalog.supportedDevice(device.model, device.version, this.project))
        throw FlankFatalError("iOS ${device.version} on ${device.model} is not a supported device")
}
