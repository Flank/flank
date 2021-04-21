package ftl.adapter

import ftl.adapter.google.GcStorage
import ftl.api.RemoteStorage
import ftl.config.FtlConstants

object GcStorageUpload :
    RemoteStorage.Upload,
    (RemoteStorage.Dir, RemoteStorage.Data) -> String by { dir, data ->
        if (data.path.startsWith(FtlConstants.GCS_PREFIX)) data.path
        else GcStorage.upload(data.path, data.bytes, dir.bucket, dir.path)
    }
