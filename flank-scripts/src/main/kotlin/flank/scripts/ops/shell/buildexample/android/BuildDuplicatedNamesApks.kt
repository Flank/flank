package flank.scripts.ops.shell.buildexample.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Files
import java.nio.file.Paths

fun AndroidBuildConfiguration.buildDuplicatedNamesApks() {
    if (artifacts.canExecute("buildDuplicatedNamesApks").not()) return
    val modules = (0..3).map { "dir$it" }

    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath) + modules.map { "$it:testModule:assembleAndroidTest" }.toList()
    ).runCommand()

    if (copy) copyDuplicatedNamesApks()
}

private fun copyDuplicatedNamesApks() {
    val modules = (0..3).map { "dir$it" }
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "duplicated_names")
    if (!outputDir.toFile().exists()) Files.createDirectories(outputDir)

    modules.map { Paths.get(androidTestProjectsPath, it, "testModule", "build", "outputs", "apk").toFile() }
        .flatMap { it.findApks().toList() }
        .forEachIndexed { index, file ->
            file.copyApkToDirectory(Paths.get(outputDir.toString(), modules[index], file.name))
        }
}
