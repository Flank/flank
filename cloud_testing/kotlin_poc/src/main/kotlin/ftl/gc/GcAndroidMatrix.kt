package ftl.gc

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceList
import com.google.api.services.testing.model.AndroidMatrix
import ftl.config.Devices

object GcAndroidMatrix {

    fun build(deviceList: List<Devices>): AndroidDeviceList = AndroidDeviceList().setAndroidDevices(
            deviceList.map {
                AndroidDevice()
                        .setAndroidModelId(it.model)
                        .setAndroidVersionId(it.version)
                        .setLocale(it.locale)
                        .setOrientation(it.orientation)
            }
    )
}
