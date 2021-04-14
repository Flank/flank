package ftl.adapter

import ftl.adapter.google.GcStorage
import ftl.data.RemoteStorage
import ftl.data.RemoteStorage.Dir

object GcStorageExists :
    RemoteStorage.Exist,
    (Dir) -> Boolean by {
        GcStorage.exist(it.bucket, it.path)
    }
