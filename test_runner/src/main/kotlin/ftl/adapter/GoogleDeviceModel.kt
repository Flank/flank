package ftl.adapter

import ftl.adapter.google.toAndroidApiModel
import ftl.adapter.google.toIosApiModel
import ftl.api.DeviceModel
import ftl.client.google.AndroidCatalog
import ftl.ios.IosCatalog

object GoogleAndroidDeviceModel :
    DeviceModel.Android.Fetch,
    (String) -> List<DeviceModel.Android> by { projectId ->
        AndroidCatalog.getModels(projectId).toAndroidApiModel()
    }

object GoogleIosDeviceModel :
    DeviceModel.Ios.Fetch,
    (String) -> List<DeviceModel.Ios> by { projectId ->
        IosCatalog.getModels(projectId).toIosApiModel()
    }
