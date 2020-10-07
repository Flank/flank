import java.nio.file.Paths

fun createGradleCommand(
    workingDir: String,
    options: List<String>
) = "${Paths.get(workingDir, "gradlew").toString()} ${options.joinToString(" ")}"
