package flank.scripts.utils

import java.nio.file.Paths

fun createGradleCommand(
    workingDir: String,
    vararg options: String?
) = createGradleCommand(workingDir, options.asList().filterNotNull())

fun createGradleCommand(
    workingDir: String,
    options: List<String>
) = "${Paths.get(workingDir, gradleExecutable)} ${options.joinToString(" ")}"

private val gradleExecutable: String
    get() = if (isWindows) "gradlew.bat" else "./gradlew"
