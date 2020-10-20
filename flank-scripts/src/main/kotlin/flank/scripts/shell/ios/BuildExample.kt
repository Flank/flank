
package flank.scripts.shell.ios

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.currentPath
import flank.scripts.shell.utils.pipe
import flank.scripts.utils.archive
import flank.scripts.utils.downloadXcPrettyIfNeeded
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

object BuildExampleCommand : CliktCommand(name = "iosBuildExample", help = "build example ios app") {
    override fun run() {
        failIfWindows()
        downloadXcPrettyIfNeeded()
        buildExample()
    }
}

private fun buildExample() {
    val dataPath: Path = Paths.get(currentPath.toString(), "dd_tmp").apply {
        toFile().deleteRecursively()
    }

    val xcodeCommandSwiftTests = createIosBuildCommand(
        dataPath.toString(),
        "./EarlGreyExample.xcworkspace",
        "EarlGreyExampleSwiftTests"
    )
    xcodeCommandSwiftTests pipe "xcpretty"

    val xcodeCommandTests = createIosBuildCommand(dataPath.toString(), "./EarlGreyExample.xcworkspace", "EarlGreyExampleTests")
    xcodeCommandTests pipe "xcpretty"

    copyExampleOutputFiles(dataPath.toString())
}

private fun copyExampleOutputFiles(dataPath: String) {
    val archiveFileName = "earlgrey_example.zip"
    val buildProductPath = Paths.get(dataPath, "Build", "Products")

    Files.walk(Paths.get(""))
        .filter { it.fileName.toString().endsWith("-iphoneos") || it.fileName.toString().endsWith(".xctestrun") }
        .map { it.toFile() }
        .collect(Collectors.toList())
        .archive(archiveFileName, currentPath.toFile())

    Files.move(
        Paths.get("", archiveFileName),
        Paths.get(buildProductPath.toString(), archiveFileName),
        StandardCopyOption.REPLACE_EXISTING
    )
}
