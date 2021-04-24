package ftl.api

import ftl.adapter.GoogleAndroidDeviceModel
import ftl.adapter.GoogleIosDeviceModel

val fetchDeviceModelAndroid: DeviceModel.Android.Fetch get() = GoogleAndroidDeviceModel
val fetchDeviceModelIos: DeviceModel.Ios.Fetch get() = GoogleIosDeviceModel

object DeviceModel {

    data class Android(
        val id: String,
        val name: String,
        val tags: List<String>,
        val screenX: Int,
        val screenY: Int,
        val formFactor: String,
        val screenDensity: Int,
        val supportedVersionIds: List<String>,
        val form: String,
        val brand: String,
        val codename: String,
        val manufacturer: String,
        val thumbnailUrl: String,
        val supportedAbis: List<String>,
        val lowFpsVideoRecording: Boolean,
    ) {

        interface Fetch : (String) -> List<Android>
    }

    data class Ios(
        val id: String,
        val name: String,
        val tags: List<String>,
        val screenX: Int,
        val screenY: Int,
        val formFactor: String,
        val screenDensity: Int,
        val supportedVersionIds: List<String>,
        val deviceCapabilities: List<String>,
    ) {

        interface Fetch : (String) -> List<Ios>
    }
}
