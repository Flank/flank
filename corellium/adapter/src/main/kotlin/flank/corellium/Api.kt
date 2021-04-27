package flank.corellium

import flank.corellium.adapter.ExecuteAndroidTestPlan
import flank.corellium.adapter.InstallAndroidApps
import flank.corellium.adapter.InvokeAndroidDevices
import flank.corellium.adapter.RequestAuthorization
import flank.corellium.api.CorelliumApi

fun corelliumApi(
    projectName: String
) = CorelliumApi(
    authorize = RequestAuthorization,
    installAndroidApps = InstallAndroidApps(
        projectName = projectName
    ),
    invokeAndroidDevices = InvokeAndroidDevices,
    executeTest = ExecuteAndroidTestPlan
)
