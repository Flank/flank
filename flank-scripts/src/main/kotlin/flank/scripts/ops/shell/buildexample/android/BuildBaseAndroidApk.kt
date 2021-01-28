package flank.scripts.ops.shell.buildexample.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun AndroidBuildConfiguration.buildBaseApk() {
    if (artifacts.canExecute("buildBaseApk").not()) return

    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app:assemble")
    ).runCommand()

    if (copy) copyBaseApk()
}

private fun copyBaseApk() {
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "app-debug.apk")

    if (!outputDir.parent.toFile().exists()) Files.createDirectories(outputDir.parent)

    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "singleSuccess", "debug", "app-single-success-debug.apk")
    Files.copy(assembleDirectory, outputDir, StandardCopyOption.REPLACE_EXISTING)
}

data class AndroidBuildConfiguration(val artifacts: List<String>, val generate: Boolean, val copy: Boolean)
