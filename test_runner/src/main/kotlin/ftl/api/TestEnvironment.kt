package ftl.api

import com.google.testing.model.ProvidedSoftwareCatalog
import ftl.adapter.GoogleTestEnvironmentAndroid
import ftl.adapter.GoogleTestEnvironmentIos

val fetchAndroidTestEnvironment: TestEnvironment.Android.Fetch get() = GoogleTestEnvironmentAndroid
val fetchIosTestEnvironment: TestEnvironment.Ios.Fetch get() = GoogleTestEnvironmentIos

object TestEnvironment {
    data class Android(
        val osVersions: List<OsVersion.Android>,
        val models: List<DeviceModel.Android>,
        val locales: List<Locale>,
        val softwareCatalog: ProvidedSoftwareCatalog,
        val networkProfiles: List<NetworkProfile>,
        val orientations: List<Orientation>,
        val ipBlocks: List<IpBlock>
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
        val ipBlocks: List<IpBlock>
    ) {
        interface Fetch : (String) -> Ios
    }
}
