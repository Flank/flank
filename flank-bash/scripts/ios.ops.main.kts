@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("GradleCommand.kt")
@file:Import("PathHelper.kt")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Paths


suspend fun Shell.generateIos() = takeUnless { isWindows }?.let {
    setupIosEnv()
}

suspend fun Shell.setupIosEnv() {
    val shouldInstallXcpretty = kotlin.runCatching { "xcpretty -V"().pcb.exitCode }.getOrDefault(1) != 0
    if (shouldInstallXcpretty) "gem install cocoapods -v 1.9.3"()
    val earlGreyExample = Paths.get(iOsTestProjectsPath, "EarlGreyExample")
    "cd $earlGreyExample && pod install")()
}

suspend fun installXcpretty() {

}

suspend fun Shell.buildEarlGreyExample() {

}
