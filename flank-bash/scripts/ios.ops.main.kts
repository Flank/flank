@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("util/GradleCommand.kt")
@file:Import("util/PathHelper.kt")
@file:Import("ios/IosBuildCommand.kt")
@file:Import("util/downloadSoftware.main.kts")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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

shell {
    if (args.contains("build")) generateIos()
}

suspend fun Shell.generateIos() = takeUnless { isWindows }?.let {
    downloadCocoaPodsIfNeeded()
    installPods(Paths.get(iOSTestProjectsPath, earlGreyExample))
    downloadXcPrettyIfNeeded()
    createDirectoryInFixture(directoryName = "objc")
    createDirectoryInFixture(directoryName = "swift")
    buildEarlGreyExample()
}

fun createDirectoryInFixture(directoryName: String): Path = Files.createDirectories(Paths.get(flankFixturesTmpPath, directoryName))

suspend fun buildEarlGreyExample() = buildDirectoryPath.runBuilds().resolve("Products").apply {
    filterFilesToCopy().copyIosProductFiles()
}.copyTestFiles()

val buildDirectoryPath = Paths.get(iOSTestProjectsPath, earlGreyExample, buildDirectory)

suspend fun Path.runBuilds() = toFile().let { projectPath ->
    projectPath.deleteRecursively()
    shell {
        val swiftCommand = createIosBuildCommand(
            projectPath.parent, Paths.get(projectPath.parent, "EarlGreyExample.xcworkspace").toString(), scheme = earlGreyExampleTests
        ).process()

        pipeline { swiftCommand pipe "xcpretty".process() }

        val objcCommand = createIosBuildCommand(projectPath.parent, Paths.get(projectPath.parent, "EarlGreyExample.xcworkspace").toString(), scheme = earlGreyExampleSwiftTests).process()
        pipeline { objcCommand pipe "xcpretty".process() }
    }
    this
}

fun Path.filterFilesToCopy() = toFile().walk().filter { it.nameWithoutExtension.endsWith("-iphoneos") || it.extension == "xctestrun" }

fun Sequence<File>.copyIosProductFiles() = forEach {
    if (it.isDirectory) it.copyRecursively(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
    else it.copyTo(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
}

fun Path.copyTestFiles() = toString().let { productsDirectory ->
    copyTestFile(productsDirectory, earlGreyExampleTests, TestType.OBJECTIVE_C)
    copyTestFile(productsDirectory, earlGreyExampleSwiftTests, TestType.SWIFT)
}

fun copyTestFile(productsDirectory: String, name: String, type: TestType) = Files.copy(
    Paths.get(productsDirectory, *pluginsDirectory, "$name.xctest", name),
    Paths.get(flankFixturesTmpPath, type.toString().toLowerCase(), name),
    StandardCopyOption.REPLACE_EXISTING
)
