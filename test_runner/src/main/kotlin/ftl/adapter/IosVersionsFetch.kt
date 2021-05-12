package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.OsVersion
import ftl.client.google.iosOsVersions

object IosVersionsFetch :
    OsVersion.Ios.Fetch,
    (String) -> OsVersion.Ios.Available by {
        iosOsVersions(it).toApiModel().available()
    }

private fun List<OsVersion.Ios>.available() = OsVersion.Ios.Available(this)
