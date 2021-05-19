import org.gradle.api.artifacts.PublishException

fun assertValidVersion(version: String) {
    val validSnapshots = arrayOf("master-SNAPSHOT", "local-SNAPSHOT")
    val versionRegex = "\\d{2}\\.\\d{2}\\.\\d{1,2}".toRegex()
    if(version !in validSnapshots && versionRegex.matches(version)) {
        throw PublishException("Maven version is not valid!")
    }
}
