package ftl.adapter

import ftl.adapter.google.GcStorage
import ftl.api.RemoteStorage

object GcStorageExists :
    RemoteStorage.Exist,
    (RemoteStorage.Dir) -> Boolean by {
        GcStorage.exist(it.bucket, it.path)
    }
