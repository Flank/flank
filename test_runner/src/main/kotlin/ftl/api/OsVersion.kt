package ftl.api

import ftl.adapter.AndroidOsVersionsFetch
import ftl.adapter.IosVersionsFetch

val fetchAndroidOsVersion: OsVersion.Android.Fetch get() = AndroidOsVersionsFetch
val fetchIosOsVersion: OsVersion.Ios.Fetch get() = IosVersionsFetch

object OsVersion {

    data class Android(
        val apiLevel: Int?,
        val codeName: String?,
        val distribution: Distribution?,
        val id: String?,
        val releaseDate: Date?,
        val tags: List<String>?,
        val versionString: String?,
    ) {
        interface Fetch : (String) -> List<Android>
    }

    data class Ios(
        val id: String?,
        val majorVersion: Int?,
        val minorVersion: Int?,
        val supportedXcodeVersionIds: List<String>?,
        val tags: List<String>?,
    ) {
        interface Fetch : (String) -> List<Ios>
    }

    data class Distribution(
        val marketShare: Double,
        val measurementTime: String,
    )

    data class Date(val day: Int?, val month: Int?, val year: Int?)
}
