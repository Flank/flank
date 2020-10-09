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
import java.nio.file.StandardCopyOption

suspend fun Shell.generateApkAndTests() {
    duplicatedNamesApks()
//    baseAppApk()
//    baseTesApks()
//    duplicatedNamesApks()
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
    File(assembleDirectory.toString()).walk().filter { it.extension == "apk" }.forEach {
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
    if (!outputDir.toFile().exists()) {
        Files.createDirectories(outputDir)
    }
    modules.map { Paths.get(androidTestProjectsPath, it, "testModule", "build", "outputs", "apk").toFile() }
        .flatMap { it.walk().filter { file -> file.extension == "apk" }.toList() }
        .forEachIndexed { index, file ->
            Paths.get(outputDir.toString(), modules[index], file.name).let { path ->
                if (!path.parent.toFile().exists()) {
                    Files.createDirectories(path.parent)
                }
                Files.copy(
                    file.toPath(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
        }
}
