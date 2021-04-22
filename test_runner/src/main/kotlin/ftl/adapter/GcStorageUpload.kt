package ftl.adapter

import ftl.adapter.google.gcStorageUpload
import ftl.api.RemoteStorage

object GcStorageUpload :
    RemoteStorage.Upload,
    (RemoteStorage.Dir, RemoteStorage.Data) -> String by { dir, data ->
        gcStorageUpload(data.path, data.bytes, dir.bucket, dir.path)
    }
