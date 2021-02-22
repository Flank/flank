@file:Suppress("unused")
package flank.corellium.sandbox.config

import java.util.Properties

object Config {
    private val prop = Properties().also { prop ->
        prop.load(object {}.javaClass.classLoader.getResourceAsStream("corellium-config.properties"))
    }

    private fun get(name: String) = prop[name] as String

    val api = get("api")

    val username = get("username")

    val password = get("password")

    val projectId = get("project_id")

    val instanceId = get("instance_id")

    val token = get("token")

    val instanceName = get("instance_name")

    val plistPath = get("plist_path")

    val pathToUploadPlist = get("path_to_upload_plist")

    val xctestrunPath = get("xctestrun_path")

    val udid = get("udid")

    val osbuild = get("osbuild")

    val flavor = get("flavor")

    val os = get("os")
}
