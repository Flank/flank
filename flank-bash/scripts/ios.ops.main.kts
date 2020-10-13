@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("util/GradleCommand.kt")
@file:Import("util/PathHelper.kt")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

suspend fun Shell.generateIos() = takeUnless { isWindows }?.let {
    setupIosEnv()
    installPods()
    installXcpretty()
    buildEarlGreyExample()
}

suspend fun Shell.setupIosEnv() {
    val shouldInstallXcpretty = kotlin.runCatching {
        "xcpretty -v"().pcb.exitCode
    }.getOrDefault(1) != 0
    if (shouldInstallXcpretty) "gem install cocoapods -v 1.9.3"()
}

suspend fun Shell.installPods() {
    val earlGreyExample = Paths.get(iOsTestProjectsPath, earlGreyExample)
    kotlin.runCatching { "pod install --project-directory=$earlGreyExample --verbose"() }
}

suspend fun Shell.installXcpretty() {
    val shouldInstallXcpretty = kotlin.runCatching { "xcpretty -v"().pcb.exitCode }.getOrDefault(1) != 0
    if (shouldInstallXcpretty) ("gem install xcpretty")()
}

suspend fun buildEarlGreyExample() = Paths.get(iOsTestProjectsPath, earlGreyExample, buildDirectory).runBuilds().let { buildDir ->
    createDirectories()
    Paths.get(buildDir.toString(), "Products")
}.apply {
    filterFilesToCopy().copyIosProductFiles()
}.copyTestFiles()

fun Path.copyTestFiles() = toString().let { productsDirectory ->
    copyTestFile(productsDirectory, earlGreyExampleTests, TestType.OBJECTIVE_C)
    copyTestFile(productsDirectory, earlGreyExampleSwiftTests, TestType.SWIFT)
}

fun copyTestFile(productsDirectory: String, name: String, type: TestType) = Files.copy(
    Paths.get(productsDirectory, *pluginsDirectory, "$name.xctest", name),
    Paths.get(flankFixturesTmpPath, type.toString().toLowerCase(), name),
    StandardCopyOption.REPLACE_EXISTING
)

suspend fun Path.runBuilds() = toFile().let { projectPath ->
    projectPath.deleteRecursively()
    shell {
        createIosBuildCommand(projectPath.parent, scheme = earlGreyExampleTests)()
        createIosBuildCommand(projectPath.parent, scheme = earlGreyExampleSwiftTests)()
    }
    this
}

fun createDirectories() {
    createDirectoryInFixture(directoryName = "objc")
    createDirectoryInFixture(directoryName = "swift")
}

fun createDirectoryInFixture(directoryName: String): Path = Files.createDirectories(Paths.get(flankFixturesTmpPath, directoryName))

fun createIosBuildCommand(buildDir: String, scheme: String) = "xcodebuild build-for-testing " +
    "-allowProvisioningUpdates " +
    "-workspace $buildDir/EarlGreyExample.xcworkspace " +
    "-scheme $scheme " +
    "-derivedDataPath $buildDir " +
    "-sdk iphoneos"

fun Path.filterFilesToCopy() = toFile().walk().filter { it.nameWithoutExtension.endsWith("-iphoneos") || it.extension == "xctestrun" }

fun Sequence<File>.copyIosProductFiles() = forEach {
    if (it.isDirectory) it.copyRecursively(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
    else it.copyTo(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
}


val debugIphoneOs = "Debug-iphoneos"
val earlGreyExampleSwift = "EarlGreyExampleSwift.app"
val plugins = "PlugIns"
val earlGreyExample = "EarlGreyExample"

val earlGreyExampleTests = "EarlGreyExampleTests"
val earlGreyExampleSwiftTests = "EarlGreyExampleSwiftTests"

val buildDirectory = "Build"

val pluginsDirectory = arrayOf(debugIphoneOs, earlGreyExampleSwift, plugins)

enum class TestType {
    SWIFT,
    OBJECTIVE_C
}
