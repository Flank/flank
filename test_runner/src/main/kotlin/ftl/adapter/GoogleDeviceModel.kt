package ftl.adapter

import ftl.adapter.google.toAndroidApiModel
import ftl.adapter.google.toIosApiModel
import ftl.api.DeviceModel
import ftl.client.google.AndroidCatalog
import ftl.client.google.IosCatalog

object GoogleAndroidDeviceModel :
    DeviceModel.Android.Fetch,
    (String) -> DeviceModel.Android.Available by { projectId ->
        AndroidCatalog.getModels(projectId).toAndroidApiModel().available()
    }

private fun List<DeviceModel.Android>.available() = DeviceModel.Android.Available(this)

object GoogleIosDeviceModel :
    DeviceModel.Ios.Fetch,
    (String) -> DeviceModel.Ios.Available by { projectId ->
        IosCatalog.getModels(projectId).toIosApiModel().available()
    }

private fun List<DeviceModel.Ios>.available() = DeviceModel.Ios.Available(this)
