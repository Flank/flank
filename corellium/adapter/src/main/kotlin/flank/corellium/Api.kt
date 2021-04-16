package flank.corellium

import flank.corellium.adapter.InstallAndroidApps
import flank.corellium.adapter.InvokeAndroidDevices
import flank.corellium.adapter.RequestAuthorization
import flank.corellium.api.CorelliumApi

fun corelliumApi(
    projectId: String
) = CorelliumApi(
    authorize = RequestAuthorization,
    installAndroidApps = InstallAndroidApps(
        projectId = projectId
    ),
    invokeAndroidDevices = InvokeAndroidDevices,
)
