import java.nio.file.Paths

fun createGradleCommand(
    workingDir: String,
    options: List<String>
) = if (isWindows) "${Paths.get(workingDir, "gradlew.bat").toString()} ${options.joinToString(" ")}"
else "${Paths.get(workingDir, "gradlew").toString()} ${options.joinToString(" ")}"

private val isWindows = System.getProperty("os.name").startsWith("Windows")
