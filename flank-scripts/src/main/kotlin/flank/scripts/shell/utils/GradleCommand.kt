package flank.scripts.shell.utils

import flank.scripts.utils.isWindows
import java.nio.file.Paths

fun createGradleCommand(
    workingDir: String,
    vararg options: String
) = createGradleCommand(workingDir, options.asList())

fun createGradleCommand(
    workingDir: String,
    options: List<String>
) = "${Paths.get(workingDir, gradleExecutable)} ${options.joinToString(" ")}"

private val gradleExecutable: String
    get() = if (isWindows) "gradlew.bat" else "./gradlew"
