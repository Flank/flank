package ftl.gc

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceList
import ftl.config.Device

object GcAndroidMatrix {

    fun build(deviceList: List<Device>): AndroidDeviceList = AndroidDeviceList().setAndroidDevices(
            deviceList.map {
                AndroidDevice()
                        .setAndroidModelId(it.model)
                        .setAndroidVersionId(it.version)
                        .setLocale(it.locale)
                        .setOrientation(it.orientation)
            }
    )
}
