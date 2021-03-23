package flank.scripts.ops.assemble.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun AndroidBuildConfiguration.buildManyTestsApk() {
    if (artifacts.canExecute("manyTestsApp").not()) return

    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app_many_tests:assemble")
    ).runCommand()

    if (copy) copyManyTestsApk()
}

private fun copyManyTestsApk() {
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "app_many_tests.apk")

    if (!outputDir.parent.toFile().exists()) Files.createDirectories(outputDir.parent)

    val assembleDirectory = Paths.get(androidTestProjectsPath, "app_many_tests", "build", "outputs", "apk", "debug", "app_many_tests-debug.apk")
    Files.copy(assembleDirectory, outputDir, StandardCopyOption.REPLACE_EXISTING)
}
