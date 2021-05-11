package ftl.adapter

import ftl.adapter.google.toAndroidApiModel
import ftl.adapter.google.toIosApiModel
import ftl.api.DeviceModel
import ftl.client.google.AndroidCatalog
import ftl.client.google.IosCatalog

object GoogleAndroidDeviceModel :
    DeviceModel.Android.Fetch,
    (String) -> List<DeviceModel.Android> by { projectId ->
        AndroidCatalog.getModels(projectId).toAndroidApiModel()
    }

object GoogleIosDeviceModel :
    DeviceModel.Ios.Fetch,
    (String) -> DeviceModel.Ios.Available by { projectId ->
        IosCatalog.getModels(projectId).toIosApiModel().available()
    }

private fun List<DeviceModel.Ios>.available() = DeviceModel.Ios.Available(this)
