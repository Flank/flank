import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.execAndGetStdout(vararg args: String): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine(*args)
        standardOutput = stdout
        workingDir = projectDir
    }
    return stdout.toString().trimEnd()
}

// TODO replace with plugin in #2063
fun Project.isVersionChangedInBuildGradle(): Boolean {

    val localResultsStream = execAndGetStdout("git", "diff", "origin/master", "HEAD", "--", "build.gradle.kts")
        .split("\n")
    val commitedResultsStream = execAndGetStdout("git", "diff", "origin/master", "--", "build.gradle.kts")
        .split("\n")
    return (commitedResultsStream + localResultsStream)
        .filter { it.startsWith("-version = ") || it.startsWith("+version = ") }
        .isNotEmpty()
}

