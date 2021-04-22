package ftl.adapter

import ftl.adapter.google.gcStorageExist
import ftl.api.RemoteStorage

object GcStorageExists :
    RemoteStorage.Exist,
    (RemoteStorage.Dir) -> Boolean by {
        gcStorageExist(it.bucket, it.path)
    }
