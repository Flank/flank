package flank.scripts.ops.common

import flank.scripts.utils.checkAndInstallIfNeed
import flank.scripts.utils.runCommand
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
