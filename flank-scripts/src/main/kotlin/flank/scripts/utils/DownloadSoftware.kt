package flank.scripts.utils

import java.nio.file.Path

fun downloadXcPrettyIfNeeded() {
    "xcpretty".checkAndInstallIfNeed("gem install xcpretty --user-install")
}

fun downloadCocoaPodsIfNeeded() {
    "pod".checkAndInstallIfNeed("gem install cocoapods -v 1.9.3 --user-install")
}

fun installPodsIfNeeded(path: Path) {
    if (path.toFile().listFiles().map { it.name }.contains("Podfile"))
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
    "generate_library".checkAndInstallIfNeed("pip install google-apis-client-generator")
}
