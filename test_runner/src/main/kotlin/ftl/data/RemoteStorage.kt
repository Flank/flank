package ftl.data

import ftl.adapter.GcStorageExists
import ftl.adapter.GcStorageUpload

val uploadToRemoteStorage: RemoteStorage.Upload get() = GcStorageUpload
val existRemoteStorage: RemoteStorage.Exist get() = GcStorageExists

object RemoteStorage {

    data class Dir(
        val bucket: String,
        val path: String
    )

    data class Data(
        val path: String,
        val bytes: ByteArray
    )

    interface Exist : (Dir) -> Boolean

    interface Upload : (Dir, Data) -> String
}
