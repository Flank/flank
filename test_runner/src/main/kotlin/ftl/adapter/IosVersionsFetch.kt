package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.OsVersion
import ftl.client.google.iosOsVersions

object IosVersionsFetch :
    OsVersion.Ios.Fetch,
    (String) -> List<OsVersion.Ios> by {
        iosOsVersions(it).toApiModel()
    }
