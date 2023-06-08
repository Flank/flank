package ftl.client.google.run.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceList
import ftl.config.Device

object GcAndroidDevice {

    fun build(deviceList: List<Device>): AndroidDeviceList = AndroidDeviceList().setAndroidDevices(
        deviceList.map {
            AndroidDevice()
                .setAndroidModelId(it.model)
                .setAndroidVersionId(it.version)
                .setLocale(it.locale)
                .setOrientation(it.orientation)
        }
    )

    fun build(device: Device): AndroidDevice = AndroidDevice()
        .setAndroidModelId(device.model)
        .setAndroidVersionId(device.version)
        .setLocale(device.locale)
        .setOrientation(device.orientation)
}
