import java.nio.file.Paths

fun createGradleCommand(
    vararg options: String
) = createGradleCommand(options.asList())

fun createGradleCommand(
    options: List<String>
) = "$gradleExecutable ${options.joinToString(" ")}"

private val gradleExecutable: String
    get() = if (isWindows) "gradlew.bat" else "./gradlew"

val isWindows = System.getProperty("os.name").startsWith("Windows")
