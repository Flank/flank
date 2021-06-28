package flank.corellium.api

/**
 * Corellium API functions.
 */
class CorelliumApi(
    val authorize: Authorization.Request = Authorization.Request { throw NotImplementedError() },
    val invokeAndroidDevices: AndroidInstance.Invoke = AndroidInstance.Invoke { throw NotImplementedError() },
    val installAndroidApps: AndroidApps.Install = AndroidApps.Install { throw NotImplementedError() },
    val executeTest: AndroidTestPlan.Execute = AndroidTestPlan.Execute { throw NotImplementedError() },
)
