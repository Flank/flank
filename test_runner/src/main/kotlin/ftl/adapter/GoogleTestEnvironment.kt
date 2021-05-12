package ftl.adapter

import ftl.api.Locale
import ftl.api.Platform
import ftl.api.TestEnvironment
import ftl.api.fetchAndroidOsVersion
import ftl.api.fetchDeviceModelAndroid
import ftl.api.fetchDeviceModelIos
import ftl.api.fetchIosOsVersion
import ftl.api.fetchIpBlocks
import ftl.api.fetchLocales
import ftl.api.fetchNetworkProfiles
import ftl.api.fetchOrientation
import ftl.api.fetchSoftwareCatalog

object FetchGoogleTestEnvironmentAndroid :
    TestEnvironment.Android.Fetch,
    (String) -> TestEnvironment.Android by { projectId ->
        TestEnvironment.Android(
            osVersions = fetchAndroidOsVersion(projectId),
            models = fetchDeviceModelAndroid(projectId).list,
            locales = fetchLocales(Locale.Identity(projectId, Platform.ANDROID)),
            softwareCatalog = fetchSoftwareCatalog(),
            networkProfiles = fetchNetworkProfiles(),
            orientations = fetchOrientation(projectId, Platform.ANDROID),
            ipBlocks = fetchIpBlocks()
        )
    }

object FetchGoogleTestEnvironmentIos :
    TestEnvironment.Ios.Fetch,
    (String) -> TestEnvironment.Ios by { projectId ->
        TestEnvironment.Ios(
            osVersions = fetchIosOsVersion(projectId).list,
            models = fetchDeviceModelIos(projectId).list,
            locales = fetchLocales(Locale.Identity(projectId, Platform.IOS)),
            softwareCatalog = fetchSoftwareCatalog(),
            networkProfiles = fetchNetworkProfiles(),
            orientations = fetchOrientation(projectId, Platform.IOS),
            ipBlocks = fetchIpBlocks(),
        )
    }
