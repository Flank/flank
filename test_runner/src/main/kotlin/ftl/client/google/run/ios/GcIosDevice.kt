package ftl.client.google.run.ios

import com.google.testing.model.IosDevice
import com.google.testing.model.IosDeviceList
import ftl.config.Device

object GcIosDevice {

    fun build(deviceList: List<Device>): IosDeviceList = IosDeviceList().setIosDevices(
        deviceList.map {
            IosDevice()
                .setIosModelId(it.model)
                .setIosVersionId(it.version)
                .setLocale(it.locale)
                .setOrientation(it.orientation)
        }
    )
}
