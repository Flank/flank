package ftl.gc.android

import com.google.api.services.testing.model.Apk
import com.google.api.services.testing.model.DeviceFile
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.IosDeviceFile
import com.google.api.services.testing.model.ObbFile
import com.google.api.services.testing.model.RegularFile

internal fun List<String>.mapGcsPathsToApks(): List<Apk>? = this
    .map { gcsPath -> Apk().setLocation(FileReference().setGcsPath(gcsPath)) }
    .takeIf { it.isNotEmpty() }

internal fun Map<String, String>.mapToDeviceFiles(): List<DeviceFile> =
    map { (devicePath: String, gcsFilePath: String) ->
        DeviceFile().setRegularFile(
            RegularFile()
                .setDevicePath(devicePath)
                .setContent(FileReference().setGcsPath(gcsFilePath))
        )
    }

internal fun Map<String, String>.mapToDeviceObbFiles(obbnames: List<String>): List<DeviceFile> {
    var index = -1 // intentional
    return map { (_, gcsFilePath: String) ->
        index++
        DeviceFile().setObbFile(
            ObbFile().setObb(FileReference().setGcsPath(gcsFilePath)).setObbFileName(obbnames[index])
        )
    }
}

internal fun Map<String, String>.mapToIosDeviceFiles(): List<IosDeviceFile> =
    map { (testDevicePath, gcsFilePath) ->
        IosDeviceFile().apply {
            if (testDevicePath.contains(":")) {
                val (bundleIdSeparated, pathSeparated) = testDevicePath.split(":")
                bundleId = bundleIdSeparated
                devicePath = pathSeparated
            } else {
                devicePath = testDevicePath
            }
            content = FileReference().setGcsPath(gcsFilePath)
        }
    }
