package flank.corellium.api

/**
 * Corellium API functions.
 */
class CorelliumApi(
    val authorize: Authorization.Request,
    val invokeAndroidDevices: AndroidInstance.Invoke,
    val installAndroidApps: AndroidApps.Install,
    val executeTest: AndroidTestPlan.Execute,
)
