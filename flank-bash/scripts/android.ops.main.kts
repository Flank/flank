@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("GradleCommand.kt")
@file:Import("PathHelper.kt")
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
    val buildBaseApk = createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app:assemble")
    )
    buildBaseApk()
    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "singleSuccess", "debug", "app-single-success-debug.apk")
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "app-debug.apk")
    if (!outputDir.parent.toFile().exists()) {
        Files.createDirectories(outputDir.parent)
    }
    Files.copy(assembleDirectory, outputDir, StandardCopyOption.REPLACE_EXISTING)

}

suspend fun Shell.baseTesApks() {
    val buildBaseTestApk = createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "app:assembleAndroidTest")
    )
    buildBaseTestApk()
    val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "androidTest")
    File(assembleDirectory.toString()).findApks().forEach {
        Files.copy(it.toPath(), Paths.get(flankFixturesTmpPath, "apk", it.name), StandardCopyOption.REPLACE_EXISTING)
    }

}

suspend fun Shell.duplicatedNamesApks() {
    val modules = (0..3).map { "dir$it" }
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath) + modules.map { "$it:testModule:assembleAndroidTest" }
    )()
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "duplicated_names")
    if (!outputDir.toFile().exists()) Files.createDirectories(outputDir)

    modules.map { Paths.get(androidTestProjectsPath, it, "testModule", "build", "outputs", "apk").toFile() }
        .flatMap { it.findApks().toList() }
        .forEachIndexed { index, file ->
            file.copyDuplicatedApkToDirectory(Paths.get(outputDir.toString(), modules[index], file.name))
        }
}

fun File.copyDuplicatedApkToDirectory(output: Path) = toPath().let { sourceFile ->
    if (!output.parent.toFile().exists()) Files.createDirectories(output.parent)
    Files.copy(sourceFile, output, StandardCopyOption.REPLACE_EXISTING)
}

suspend fun Shell.multiModulesApks() {
    val modulesName = (1..20).map { "testModule$it" }
    val gradleModules = modulesName.map { ":multi-modules:$it:assembleAndroidTest" }
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, ":multi-modules:multiapp:assemble") + gradleModules
    )()
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "multi-modules").toString()
    Paths.get(androidTestProjectsPath, "multi-modules").toFile().findApks()
        .forEach {
            it.copyDuplicatedApkToDirectory(Paths.get(outputDir, it.name))
        }

}

suspend fun Shell.cucumberSampleApp() {
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf("-p", androidTestProjectsPath, "cucumber_sample_app:cukeulator:assembleDebug", ":cucumber_sample_app:cukeulator:assembleAndroidTest")
    )()
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", "cucumber_sample_app").toString()
    Paths.get(androidTestProjectsPath, "cucumber_sample_app").toFile().findApks()
        .copyApksToPath(outputDir)
}

fun File.findApks() = walk().filter { it.extension == "apk" }

fun Sequence<File>.copyApksToPath(outputDirectory: String) = forEach {
    it.copyDuplicatedApkToDirectory(Paths.get(outputDirectory, it.name))
}
