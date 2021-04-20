package ftl.adapter

import ftl.adapter.google.GcStorage
import ftl.adapter.google.existsToApiModel
import ftl.api.RemoteStorage

object GcStorageExists :
    RemoteStorage.Exist,
    (RemoteStorage.Dir) -> Boolean by {
        GcStorage.existsToApiModel(it.bucket, it.path)
    }
