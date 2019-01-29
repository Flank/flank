package ftl.gc

import com.google.api.services.testing.model.IosDevice
import com.google.api.services.testing.model.IosDeviceList
import ftl.config.Device

object GcIosMatrix {

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
