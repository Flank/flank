package flank.scripts.ops.shell.ops.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.ops.shell.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun AndroidBuildConfiguration.buildBaseTestApk() {
    if (artifacts.canExecute("buildBaseTestApk").not()) return
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app:assembleAndroidTest")
    ).runCommand()

    if (copy) copyBaseTestApk()
}

private fun copyBaseTestApk() {
    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "androidTest")
    assembleDirectory.toFile().findApks().forEach {
        Files.copy(it.toPath(), Paths.get(flankFixturesTmpPath, "apk", it.name), StandardCopyOption.REPLACE_EXISTING)
    }
}
