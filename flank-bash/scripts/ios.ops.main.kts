@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("GradleCommand.kt")
@file:Import("PathHelper.kt")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
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
    val buildDir = Paths.get(iOsTestProjectsPath, "EarlGreyExample", "build")
    ("rm -rf \"${buildDir}\"")()
    val cmd =
        ("xcodebuild build-for-testing -allowProvisioningUpdates -workspace ${buildDir.parent}/EarlGreyExample.xcworkspace -scheme EarlGreyExampleSwiftTests -derivedDataPath ${buildDir.parent} -sdk iphoneos")
    println(cmd)
    cmd()


//    ("""
//        xcodebuild build-for-testing
//        -allowProvisioningUpdates
//        -workspace '${buildDir.parent}/EarlGreyExample.xcworkspace'
//        -scheme 'EarlGreyExampleTests'
//        -derivedDataPath '${buildDir.parent}'
//        -sdk iphoneos | xcpretty
//    """.trimIndent())()

    val productsDir = Paths.get(buildDir.toString(), "Build", "Products")
    productsDir.toFile().walk().filter {
        it.nameWithoutExtension.endsWith("-iphoneos") || it.extension == "xctestrun"
    }.forEach {
        Files.copy(it.toPath(), Paths.get(flankFixturesTmpPath, it.name), StandardCopyOption.REPLACE_EXISTING)
    }

}

suspend fun Shell.installXcpretty() {
    val shouldInstallXcpretty = kotlin.runCatching { "xcpretty -v"().pcb.exitCode }.getOrDefault(1) != 0
    if (shouldInstallXcpretty) ("gem install xcpretty")()
}
