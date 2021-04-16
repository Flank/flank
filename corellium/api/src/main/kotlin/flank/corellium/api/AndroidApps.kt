package flank.corellium.api

/**
 * The [AndroidApps] is representing a bunch of apk files related to the testing instance.
 *
 * @property instanceId the id of the instance where the apps will be installed.
 * @property paths the list of relative paths to the app (apk) files.
 */
data class AndroidApps(
    val instanceId: String,
    val paths: List<String>
) {

    /**
     * Install android apps on the specified instances.
     */
    interface Install : Request<List<AndroidApps>, Unit>
}
