package ftl.adapter

import ftl.api.RemoteStorage
import ftl.client.google.gcStorageUpload

object GcStorageFileUpload :
    RemoteStorage.FileUpload,
        (RemoteStorage.Dir, RemoteStorage.File) -> String by { dir, file ->
        gcStorageUpload(file.path, dir.bucket, dir.path)
    }