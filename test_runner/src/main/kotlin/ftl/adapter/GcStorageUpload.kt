package ftl.adapter

import ftl.adapter.google.GcStorage
import ftl.data.RemoteStorage

object GcStorageUpload :
    RemoteStorage.Upload,
    (RemoteStorage.Dir, RemoteStorage.Data) -> Unit by { dir, data ->
        GcStorage.upload(data.path, data.bytes, dir.bucket, dir.path)
    }
