package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.OsVersion
import ftl.client.google.androidOsVersions

object AndroidOsVersionsFetch :
    OsVersion.Android.Fetch,
    (String) -> List<OsVersion.Android> by {
        androidOsVersions(it).toApiModel()
    }
