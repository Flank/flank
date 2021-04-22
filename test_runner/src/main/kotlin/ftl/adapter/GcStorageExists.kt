package ftl.adapter

import ftl.api.RemoteStorage
import ftl.client.google.gcStorageExist

object GcStorageExists :
    RemoteStorage.Exist,
    (RemoteStorage.Dir) -> Boolean by {
        gcStorageExist(it.bucket, it.path)
    }
