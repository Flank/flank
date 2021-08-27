package flank.corellium

import flank.corellium.adapter.executeAndroidTestPlan
import flank.corellium.adapter.getCorellium
import flank.corellium.adapter.getRateInfo
import flank.corellium.adapter.installAndroidApps
import flank.corellium.adapter.invokeAndroidDevices
import flank.corellium.adapter.requestAuthorization
import flank.corellium.api.CorelliumApi

fun corelliumApi(
    projectName: String
) = CorelliumApi(
    authorize = requestAuthorization,
    installAndroidApps = installAndroidApps(
        projectName = projectName
    ),
    invokeAndroidDevices = invokeAndroidDevices(
        projectName = projectName
    ),
    executeTest = executeAndroidTestPlan(getCorellium),
    getRate = getRateInfo(getCorellium),
)
