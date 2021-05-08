package flank.corellium

import flank.corellium.adapter.executeAndroidTestPlan
import flank.corellium.adapter.installAndroidApps
import flank.corellium.adapter.invokeAndroidDevices
import flank.corellium.adapter.parseApkInfo
import flank.corellium.adapter.parseApkPackageName
import flank.corellium.adapter.parseApkTestCases
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
    executeTest = executeAndroidTestPlan,
    parseTestCases = parseApkTestCases,
    parseTestApkInfo = parseApkInfo,
    parsePackageName = parseApkPackageName
)
