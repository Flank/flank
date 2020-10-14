@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("util/GradleCommand.kt")
@file:Import("util/PathHelper.kt")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Path
import java.nio.file.StandardCopyOption

suspend fun Shell.generateApkAndTests() {
    baseAppApk()
    baseTesApks()
    duplicatedNamesApks()
    multiModulesApks()
    cucumberSampleApp()
}

suspend fun Shell.baseAppApk() {
    shell(dir = rootDirectoryFile) {
        createGradleCommand("app:assemble")()
    }
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "app-debug.apk")
    if (!outputDir.parent.toFile().exists()) Files.createDirectories(outputDir.parent)
    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "singleSuccess", "debug", "app-single-success-debug.apk")
    Files.copy(assembleDirectory, outputDir, StandardCopyOption.REPLACE_EXISTING)

}

suspend fun Shell.baseTesApks() {
    shell(dir = rootDirectoryFile) {
        createGradleCommand("app:assembleAndroidTest")()
    }
    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "androidTest")
    File(assembleDirectory.toString()).findApks().forEach {
        Files.copy(it.toPath(), Paths.get(flankFixturesTmpPath, "apk", it.name), StandardCopyOption.REPLACE_EXISTING)
    }
}

suspend fun Shell.duplicatedNamesApks() {
    val modules = (0..3).map { "dir$it" }
    shell(dir = rootDirectoryFile) {
        createGradleCommand(modules.map { "$it:testModule:assembleAndroidTest" })()
    }
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "duplicated_names")
    if (!outputDir.toFile().exists()) Files.createDirectories(outputDir)

    modules.map { Paths.get(androidTestProjectsPath, it, "testModule", "build", "outputs", "apk").toFile() }
        .flatMap { it.findApks().toList() }
        .forEachIndexed { index, file ->
            file.copyApkToDirectory(Paths.get(outputDir.toString(), modules[index], file.name))
        }
}

fun File.copyApkToDirectory(output: Path) = toPath().let { sourceFile ->
    if (!output.parent.toFile().exists()) Files.createDirectories(output.parent)
    Files.copy(sourceFile, output, StandardCopyOption.REPLACE_EXISTING)
}

suspend fun Shell.multiModulesApks() {
    shell(dir = rootDirectoryFile) {
        createGradleCommand(listOf(":multi-modules:multiapp:assemble") + (1..20).map { ":multi-modules:testModule$it:assembleAndroidTest" })()
    }
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "multi-modules").toString()
    Paths.get(androidTestProjectsPath, "multi-modules").toFile().findApks()
        .forEach { it.copyApkToDirectory(Paths.get(outputDir, it.name)) }
}

suspend fun Shell.cucumberSampleApp() {
    shell(dir = rootDirectoryFile) {
        createGradleCommand("cucumber_sample_app:cukeulator:assembleDebug", ":cucumber_sample_app:cukeulator:assembleAndroidTest")()
    }
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "cucumber_sample_app").toString()
    Paths.get(androidTestProjectsPath, "cucumber_sample_app").toFile().findApks().copyApksToPath(outputDir)
}

fun File.findApks() = walk().filter { it.extension == "apk" }

fun Sequence<File>.copyApksToPath(outputDirectory: String) = forEach {
    it.copyApkToDirectory(Paths.get(outputDirectory, it.name))
}
