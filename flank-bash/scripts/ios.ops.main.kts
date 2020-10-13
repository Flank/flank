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
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


suspend fun Shell.generateIos() = takeUnless { isWindows }?.let {
    setupIosEnv()
    installPods()
    buildEarlGreyExample()
}

suspend fun Shell.setupIosEnv() {
    val shouldInstallXcpretty = kotlin.runCatching {
        val result = "xcpretty -v"()
        result.pcb.exitCode
    }.getOrDefault(1) != 0
    if (shouldInstallXcpretty) "gem install cocoapods -v 1.9.3"()
}

suspend fun Shell.installPods() {
    val earlGreyExample = Paths.get(iOsTestProjectsPath, "EarlGreyExample")
    kotlin.runCatching { "pod install --project-directory=$earlGreyExample --verbose"() }
}

suspend fun Shell.buildEarlGreyExample() {
    installXcpretty()
    val buildDir = Paths.get(iOsTestProjectsPath, "EarlGreyExample", "Build")
    buildDir.toFile().deleteRecursively()

    ("xcodebuild build-for-testing " +
        "-allowProvisioningUpdates " +
        "-workspace ${buildDir.parent}/EarlGreyExample.xcworkspace " +
        "-scheme EarlGreyExampleSwiftTests " +
        "-derivedDataPath ${buildDir.parent} " +
        "-sdk iphoneos")()


    ("xcodebuild build-for-testing " +
        "-allowProvisioningUpdates " +
        "-workspace ${buildDir.parent}/EarlGreyExample.xcworkspace " +
        "-scheme EarlGreyExampleTests " +
        "-derivedDataPath ${buildDir.parent} " +
        "-sdk iphoneos")()

    Files.createDirectories(Paths.get(flankFixturesTmpPath, "objc"))
    Files.createDirectories(Paths.get(flankFixturesTmpPath, "swift"))
    val productsDir = Paths.get(buildDir.toString(), "Products")
    productsDir.toFile().walk().filter {
        it.nameWithoutExtension.endsWith("-iphoneos") || it.extension == "xctestrun"
    }.forEach {
        println(it.name)
        if (it.isDirectory) it.copyRecursively(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
        else it.copyTo(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
    }
    Files.copy(Paths.get(productsDir.toString(), "Debug-iphoneos", "EarlGreyExampleSwift.app", "PlugIns", "EarlGreyExampleTests.xctest", "EarlGreyExampleTests"), Paths.get(flankFixturesTmpPath, "objc", "EarlGreyExampleTests"), StandardCopyOption.REPLACE_EXISTING)
    Files.copy(Paths.get(productsDir.toString(), "Debug-iphoneos", "EarlGreyExampleSwift.app", "PlugIns", "EarlGreyExampleSwiftTests.xctest", "EarlGreyExampleSwiftTests"), Paths.get(flankFixturesTmpPath, "swift", "EarlGreyExampleSwiftTests"), StandardCopyOption.REPLACE_EXISTING)
}

suspend fun Shell.installXcpretty() {
    val shouldInstallXcpretty = kotlin.runCatching { "xcpretty -v"().pcb.exitCode }.getOrDefault(1) != 0
    if (shouldInstallXcpretty) ("gem install xcpretty")()
}
