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

val buildBaseApk = createGradleCommand(
    workingDir = androidTestProjectsPath,
    options = listOf("-p", androidTestProjectsPath, "app:assemble")
)

val buildBaseTestApk = createGradleCommand(
    workingDir = androidTestProjectsPath,
    options = listOf("-p", androidTestProjectsPath, "app:assembleAndroidTest")
)

fun generateApkAndTests() {
    baseAppApk()
    baseTesApks()
}

fun baseAppApk() {
    shell {
        buildBaseApk()
        val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "singleSuccess", "debug", "app-single-success-debug.apk")
        val outputDir = Paths.get(flankFixturesTmpPath, "apk", "app-debug.apk")
        if(!outputDir.parent.toFile().exists()){
            Files.createDirectories(outputDir.parent)
        }
        Files.copy(assembleDirectory, outputDir, StandardCopyOption.REPLACE_EXISTING)
    }
}

fun baseTesApks() {
    shell {
        buildBaseTestApk()
        val assembleDirectory = Paths.get(androidTestProjectsPath, "app", "build", "outputs", "apk", "androidTest")
        File(assembleDirectory.toString()).walk().filter { it.extension == "apk" }.forEach {
            File("test.log").appendText("Test FILE: ${it.toPath()}")
            Files.copy(it.toPath(), Paths.get(flankFixturesTmpPath, "apk", it.name))
        }
    }
}
