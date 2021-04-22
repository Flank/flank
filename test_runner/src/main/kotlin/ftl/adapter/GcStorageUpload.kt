package ftl.adapter

import ftl.api.RemoteStorage
import ftl.client.google.gcStorageUpload

object GcStorageUpload :
    RemoteStorage.Upload,
    (RemoteStorage.Dir, RemoteStorage.Data) -> String by { dir, data ->
        gcStorageUpload(data.path, data.bytes, dir.bucket, dir.path)
    }
