package flank.scripts.ops.shell.buildexample.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.io.File
import java.nio.file.Paths

fun AndroidBuildConfiguration.buildCucumberSampleApp() {
    if (artifacts.canExecute("buildMultiModulesApks").not()) return
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "cucumber_sample_app:cukeulator:assembleDebug", ":cucumber_sample_app:cukeulator:assembleAndroidTest")
    ).runCommand()

    if (copy) copyCucumberSampleApp()
}

private fun copyCucumberSampleApp() {
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "cucumber_sample_app").toString()
    Paths.get(androidTestProjectsPath, "cucumber_sample_app").toFile().findApks().copyApksToPath(outputDir)
}

internal fun Sequence<File>.copyApksToPath(outputDirectory: String) = forEach {
    it.copyApkToDirectory(Paths.get(outputDirectory, it.name))
}
