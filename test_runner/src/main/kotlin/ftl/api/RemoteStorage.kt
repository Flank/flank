package ftl.api

import ftl.adapter.GcStorageExists
import ftl.adapter.GcStorageFileUpload
import ftl.adapter.GcStorageUpload

val uploadToRemoteStorage: RemoteStorage.Upload get() = GcStorageUpload
val uploadFileToRemoteStorage: RemoteStorage.FileUpload get() = GcStorageFileUpload
val existRemoteStorage: RemoteStorage.Exist get() = GcStorageExists

object RemoteStorage {

    data class Dir(
        val bucket: String,
        val path: String
    )

    data class Data(
        val path: String,
        val bytes: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Data

            if (path != other.path) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    data class File(
        val path: String
    )

    interface Exist : (Dir) -> Boolean

    interface Upload : (Dir, Data) -> String

    interface FileUpload : (Dir, File) -> String
}
