package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.androidTestProjectsPath
import flank.scripts.shell.utils.createGradleCommand
import flank.scripts.shell.utils.flankFixturesTmpPath
import flank.scripts.utils.runCommand
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class AndroidOpsCommand : CliktCommand(name = "android", help = "Build android apk's with test's") {
    override fun run() {
        buildBaseApk()
        buildBaseTestApk()
        buildDuplicatedNamesApks()
        buildMultiModulesApks()
        buildCucumberSampleApp()
    }
}

private fun buildBaseApk() {
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app:assemble")
    ).runCommand()

    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "app-debug.apk")

    if (!outputDir.parent.toFile().exists()) Files.createDirectories(outputDir.parent)

    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "singleSuccess", "debug", "app-single-success-debug.apk")
    Files.copy(assembleDirectory, outputDir, StandardCopyOption.REPLACE_EXISTING)
}

private fun buildBaseTestApk() {
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app:assembleAndroidTest")
    ).runCommand()

    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "androidTest")
    assembleDirectory.toFile().findApks().forEach {
        Files.copy(it.toPath(), Paths.get(flankFixturesTmpPath, "apk", it.name), StandardCopyOption.REPLACE_EXISTING)
    }
}

private fun buildDuplicatedNamesApks() {
    val modules = (0..3).map { "dir$it" }

    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath) + modules.map { "$it:testModule:assembleAndroidTest" }.toList()
    ).runCommand()

    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "duplicated_names")
    if (!outputDir.toFile().exists()) Files.createDirectories(outputDir)

    modules.map { Paths.get(androidTestProjectsPath, it, "testModule", "build", "outputs", "apk").toFile() }
        .flatMap { it.findApks().toList() }
        .forEachIndexed { index, file ->
            file.copyApkToDirectory(Paths.get(outputDir.toString(), modules[index], file.name))
        }
}

private fun File.copyApkToDirectory(output: Path): Path = toPath().let { sourceFile ->
    if (!output.parent.toFile().exists()) Files.createDirectories(output.parent)
    Files.copy(sourceFile, output, StandardCopyOption.REPLACE_EXISTING)
}

private fun buildMultiModulesApks() {
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath,
            ":multi-modules:multiapp:assemble") + (1..20).map { ":multi-modules:testModule$it:assembleAndroidTest" }).runCommand()

    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "multi-modules").toString()
    Paths.get(androidTestProjectsPath, "multi-modules").toFile().findApks()
        .forEach { it.copyApkToDirectory(Paths.get(outputDir, it.name)) }
}

private fun buildCucumberSampleApp() {
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "cucumber_sample_app:cukeulator:assembleDebug", ":cucumber_sample_app:cukeulator:assembleAndroidTest")
    ).runCommand()

    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "cucumber_sample_app").toString()
    Paths.get(androidTestProjectsPath, "cucumber_sample_app").toFile().findApks().copyApksToPath(outputDir)
}

private fun File.findApks() = walk().filter { it.extension == "apk" }

private fun Sequence<File>.copyApksToPath(outputDirectory: String) = forEach {
    it.copyApkToDirectory(Paths.get(outputDirectory, it.name))
}
