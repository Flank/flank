package ftl.gc.android

import com.google.testing.model.Apk
import com.google.testing.model.DeviceFile
import com.google.testing.model.FileReference
import com.google.testing.model.IosDeviceFile
import com.google.testing.model.ObbFile
import com.google.testing.model.RegularFile

internal fun List<String>.mapGcsPathsToApks(): List<Apk>? = this
    .map { gcsPath -> Apk().setLocation(gcsPath.toFileReference()) }
    .takeIf { it.isNotEmpty() }

internal fun List<String>.mapGcsPathsToFileReference(): List<FileReference> = map { it.toFileReference() }

private fun String.toFileReference() = FileReference().setGcsPath(this)

internal fun Map<String, String>.mapToDeviceFiles(): List<DeviceFile> =
    map { (devicePath: String, gcsFilePath: String) ->
        DeviceFile().setRegularFile(
            RegularFile()
                .setDevicePath(devicePath)
                .setContent(gcsFilePath.toFileReference())
        )
    }

internal fun Map<String, String>.mapToDeviceObbFiles(obbnames: List<String>): List<DeviceFile> {
    return values.mapIndexed { index, gcsFilePath ->
        DeviceFile().setObbFile(
            ObbFile().setObb(FileReference().setGcsPath(gcsFilePath)).setObbFileName(obbnames[index])
        )
    }
}

internal fun Map<String, String>.mapToIosDeviceFiles(): List<IosDeviceFile> = map { (testDevicePath, gcsFilePath) -> toIosDeviceFile(testDevicePath, gcsFilePath) }

internal fun toIosDeviceFile(testDevicePath: String, gcsFilePath: String = "") = IosDeviceFile().apply {
    if (testDevicePath.contains(":")) {
        val (bundleIdSeparated, pathSeparated) = testDevicePath.split(":")
        bundleId = bundleIdSeparated
        devicePath = pathSeparated
    } else {
        devicePath = testDevicePath
    }
    content = FileReference().setGcsPath(gcsFilePath)
}
