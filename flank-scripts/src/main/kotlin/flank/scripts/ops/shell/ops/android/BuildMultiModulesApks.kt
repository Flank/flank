package flank.scripts.ops.shell.ops.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.ops.shell.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Paths

fun AndroidBuildConfiguration.buildMultiModulesApks() {
    if (artifacts.canExecute("buildMultiModulesApks").not()) return
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf(
            "-p", androidTestProjectsPath,
            ":multi-modules:multiapp:assemble"
        ) + (1..20).map { ":multi-modules:testModule$it:assembleAndroidTest" }
    ).runCommand()

    if (copy) copyMultiModulesApks()
}

private fun copyMultiModulesApks() {
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "multi-modules").toString()
    Paths.get(androidTestProjectsPath, "multi-modules").toFile().findApks()
        .forEach { it.copyApkToDirectory(Paths.get(outputDir, it.name)) }
}
