@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("downloadFiles.main.kts")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.Shell
import eu.jrie.jetbrains.kotlinshell.shell.shell
import java.io.File
import java.nio.file.Path

suspend fun Shell.commandExitCode(command: String) = runCatching { command().pcb.exitCode }.getOrDefault(1)

fun checkAndInstall(
    checkCommand: String,
    install: suspend Shell.() -> Unit
) {
    shell {
        if (commandExitCode(checkCommand) != 0) {
            install()
        }
    }
}

fun downloadXcPrettyIfNeeded() {
    checkAndInstall(checkCommand = "command -v xcpretty") {
        "gem install xcpretty"()
    }
}

fun downloadCocoaPodsIfNeeded() {
    checkAndInstall(checkCommand = "command -v xcpretty") {
        "gem install cocoapods -v 1.9.3"()
    }
}

suspend fun Shell.installPods(path: Path) {
    kotlin.runCatching { "pod install --project-directory=$path --verbose"() }
}

fun downloadPipIfNeeded() {

    checkAndInstall("pip -V") {
        val pipOutputFile = File("get-pip.py")
        downloadFile(
            url = "https://bootstrap.pypa.io/get-pip.py",
            destinationPath = pipOutputFile.absolutePath
        )
        "python get-pip.py"()

        pipOutputFile.delete()
    }
}

fun downloadSortJsonIfNeeded() {
    checkAndInstall("sort-json") {
        "npm -g install sort-json"()
    }
}
