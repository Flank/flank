package flank.corellium.sandbox.config

import java.nio.file.Paths
import java.util.Properties

object Config {
    private val prop = Properties().also { prop ->
        with(Paths.get(object {}.javaClass.getResource("corellium-config.properties").toURI()).toFile()) {
            prop.load(inputStream())
        }
    }

    val api: String
        get() = prop["api"] as String

    val username: String
        get() = prop["username"] as String

    val password: String
        get() = prop["password"] as String

    val projectId: String
        get() = prop["project_id"] as String

    val instanceId: String
        get() = prop["instance_id"] as String

    val token: String
        get() = prop["token"] as String
}
