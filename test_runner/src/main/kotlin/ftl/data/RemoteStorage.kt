package ftl.data

import ftl.adapter.GcStorageExists

val uploadToRemoteStorage: RemoteStorage.Upload get() = TODO()
val existRemoteStorage: RemoteStorage.Exist get() = GcStorageExists

object RemoteStorage {

    data class Dir(
        val bucket: String,
        val path: String
    )

    class Data(
        val path: String,
        val bytes: ByteArray? = null // Use, when file under the given path doesn't exist.
    )

    interface Exist : (Dir) -> Boolean

    interface Upload : (Dir, Data) -> Unit
}
