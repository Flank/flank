package ftl.adapter

import ftl.api.Locale
import ftl.api.Platform
import ftl.api.TestEnvironment
import ftl.api.fetchAndroidOsVersion
import ftl.api.fetchDeviceModelAndroid
import ftl.api.fetchIpBlocks
import ftl.api.fetchLocales
import ftl.api.fetchNetworkProfiles
import ftl.api.fetchOrientation
import ftl.api.fetchSoftwareCatalog

object GoogleTestEnvironmentAndroid :
    TestEnvironment.Android.Fetch,
    (String) -> TestEnvironment.Android by { projectId ->
        TestEnvironment.Android(
            osVersions = fetchAndroidOsVersion(projectId),
            models = fetchDeviceModelAndroid(projectId),
            locales = fetchLocales(Locale.Identity(projectId, Platform.ANDROID)),
            softwareCatalog = fetchSoftwareCatalog(),
            networkProfiles = fetchNetworkProfiles(),
            orientations = fetchOrientation(projectId, Platform.ANDROID),
            ipBlocks = fetchIpBlocks()
        )
    }

// todo this is just for linter to be happy
object GoogleTestEnvironmentIos :
    TestEnvironment.Ios.Fetch,
    (String) -> TestEnvironment.Ios by { TODO() }
