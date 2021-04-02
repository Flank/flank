@file:Suppress("unused")
package flank.corellium.sandbox.config

import java.util.Properties

object Config {
    private val prop = Properties().also { prop ->
        prop.load(object {}.javaClass.classLoader.getResourceAsStream("corellium-config.properties"))
    }

    fun getProp(name: String) = prop[name] as String

    val api = getProp("api")

    val username = getProp("username")

    val password = getProp("password")

    val projectId = getProp("project_id")

    val instanceId = getProp("instance_id")

    val token = getProp("token")

    val instanceName = getProp("instance_name")

    val plistPath = getProp("plist_path")

    val pathToUploadPlist = getProp("path_to_upload_plist")

    val xctestrunPath = getProp("xctestrun_path")

    val udid = getProp("udid")

    val osbuild = getProp("osbuild")

    val flavor = getProp("flavor")

    val os = getProp("os")

    val screen = getProp("screen")

    val apkPath = getProp("apk_path")

    val testApkPath = getProp("test_apk_path")
}
