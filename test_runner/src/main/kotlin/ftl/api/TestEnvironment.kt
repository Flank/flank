package ftl.api

import com.google.api.services.testing.model.ProvidedSoftwareCatalog
import ftl.adapter.FetchGoogleTestEnvironmentAndroid
import ftl.adapter.FetchGoogleTestEnvironmentIos

val fetchAndroidTestEnvironment: TestEnvironment.Android.Fetch get() = FetchGoogleTestEnvironmentAndroid
val fetchIosTestEnvironment: TestEnvironment.Ios.Fetch get() = FetchGoogleTestEnvironmentIos

object TestEnvironment {
    data class Android(
        val osVersions: List<OsVersion.Android>,
        val models: List<DeviceModel.Android>,
        val locales: List<Locale>,
        val softwareCatalog: ProvidedSoftwareCatalog,
        val networkProfiles: List<NetworkProfile>,
        val orientations: List<Orientation>,
        val ipBlocks: IpBlockList
    ) {
        interface Fetch : (String) -> Android
    }

    data class Ios(
        val osVersions: List<OsVersion.Ios>,
        val models: List<DeviceModel.Ios>,
        val locales: List<Locale>,
        val softwareCatalog: ProvidedSoftwareCatalog,
        val networkProfiles: List<NetworkProfile>,
        val orientations: List<Orientation>,
        val ipBlocks: IpBlockList
    ) {
        interface Fetch : (String) -> Ios
    }
}
