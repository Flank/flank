package flank.scripts.utils

import java.nio.file.Path

fun downloadXcPrettyIfNeeded() {
    "xcpretty".checkAndInstallIfNeed("gem install xcpretty")
}

fun downloadCocoaPodsIfNeeded() {
    "xcpretty".checkAndInstallIfNeed("gem install cocoapods -v 1.9.3")
}

fun installPods(path: Path) {
    "pod install --project-directory=$path --verbose".runCommand()
}

fun checkIfPipInstalled() {
    "pip".commandInstalledOr {
        println("You need pip fot this script. To install it follow https://pip.pypa.io/en/stable/installing/")
    }
}

fun downloadSortJsonIfNeeded() {
    "sort-json".checkAndInstallIfNeed("npm -g install sort-json")
}

fun installClientGeneratorIfNeeded() {
    val generateLibraryCheckCommand = (if (isWindows) "where " else "command -v ") + "generate_library"
    generateLibraryCheckCommand.checkAndInstallIfNeed("pip install google-apis-client-generator")
}
