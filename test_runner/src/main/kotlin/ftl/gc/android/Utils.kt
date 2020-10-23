package ftl.gc.android

import com.google.api.services.testing.model.Apk
import com.google.api.services.testing.model.DeviceFile
import com.google.api.services.testing.model.FileReference
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

internal fun List<String>.mapToDeviceFiles(): List<DeviceFile> =
    filterNotNull().map {
        DeviceFile().setObbFile(
            ObbFile().setObb(FileReference().setGcsPath(it))
        )
    }
