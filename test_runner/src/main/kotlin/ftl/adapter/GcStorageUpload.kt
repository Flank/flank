package ftl.adapter

import ftl.adapter.google.GcStorage
import ftl.adapter.google.uploadToApiModel
import ftl.api.RemoteStorage

object GcStorageUpload :
    RemoteStorage.Upload,
    (RemoteStorage.Dir, RemoteStorage.Data) -> String by { dir, data ->
        GcStorage.uploadToApiModel(data.path, data.bytes, dir.bucket, dir.path)
    }
