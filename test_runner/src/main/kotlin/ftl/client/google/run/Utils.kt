package ftl.client.google.run

import com.google.api.services.testing.model.ClientInfoDetail
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.IosDeviceFile
import flank.tool.resource.readRevision
import flank.tool.resource.readVersion

internal fun List<String>.mapGcsPathsToFileReference(): List<FileReference> = map { it.toFileReference() }

fun String.toFileReference(): FileReference = FileReference().setGcsPath(this)

internal fun Map<String, String>.mapToIosDeviceFiles(): List<IosDeviceFile> =
    map { (testDevicePath, gcsFilePath) -> toIosDeviceFile(testDevicePath, gcsFilePath) }

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

internal fun Map<String, String>?.toClientInfoDetailList() =
    (this ?: emptyMap()).map { (key, value) ->
        ClientInfoDetail()
            .setKey(key)
            .setValue(value)
    }.addFlankVersionInfo()

private fun List<ClientInfoDetail>.addFlankVersionInfo() = plus(
    ClientInfoDetail()
        .setKey("Flank Version")
        .setValue(readVersion())
).plus(
    ClientInfoDetail()
        .setKey("Flank Revision")
        .setValue(readRevision())
)
