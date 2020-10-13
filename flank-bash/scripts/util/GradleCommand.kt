import java.nio.file.Paths

fun createGradleCommand(
    workingDir: String,
    options: List<String>
) = "${Paths.get(workingDir, gradleExecutable).toString()} ${options.joinToString(" ")}"

private val gradleExecutable: String
    get() = if(isWindows) "gradlew.bat" else "gradlew"

val isWindows = System.getProperty("os.name").startsWith("Windows")
