@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")
@file:Import("downloadFiles.main.kts")
@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.Shell
import eu.jrie.jetbrains.kotlinshell.shell.shell
import java.nio.file.Path
import kotlin.system.exitProcess

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
    runCatching { "pod install --project-directory=$path --verbose"() }
}

fun checkIfPipInstalled() {
    shell {
        if (commandExitCode("pip -V") != 0) {
            println("You need pip fot this script. To install it follow https://pip.pypa.io/en/stable/installing/")
            exitProcess(1)
        }
    }
}

fun downloadSortJsonIfNeeded() {
    checkAndInstall("sort-json") {
        "npm -g install sort-json"()
    }
}

fun installClientGeneratorIfNeeded() {
    val isWindows = System.getProperty("os.name").startsWith("Windows")
    val generateLibraryCheckCommand = (if (isWindows) "where " else "command -v ") + "generate_library"

    checkAndInstall(generateLibraryCheckCommand) {
        checkIfPipInstalled()
        "pip install google-apis-client-generator"()
    }
}
