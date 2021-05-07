package flank.corellium.api

/**
 * Corellium API functions.
 */
class CorelliumApi(
    val authorize: Authorization.Request,
    val invokeAndroidDevices: AndroidInstance.Invoke,
    val installAndroidApps: AndroidApps.Install,
    val executeTest: AndroidTestPlan.Execute,
    val parseTestCases: Apk.ParseTestCases,
    val parsePackageName: Apk.ParsePackageName,
    val parseTestApkInfo: Apk.ParseInfo,
)
