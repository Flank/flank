package flank.scripts.shell.ios

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.pipe
import flank.scripts.utils.archive
import flank.scripts.utils.downloadXcPrettyIfNeeded
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

object BuildFtlCommand : CliktCommand(name = "iosBuildFtl", help = "build ftl ios app") {
    override fun run() {
        failIfWindows()
        downloadXcPrettyIfNeeded()
        buildFtl()
    }
}

private fun buildFtl() {
    val dataPath = Paths.get("", "dd_tmp").apply {
        toFile().deleteRecursively()
    }.toString()
    val xcodeCommand = createIosBuildCommand(dataPath, "./EarlGreyExample.xcworkspace", "\"EarlGreyExampleSwiftTests\"")

    xcodeCommand pipe "xcpretty"
    copyFtlOutputFiles(dataPath)
}

private fun copyFtlOutputFiles(dataPath: String) {
    val archiveFileName = "earlgrey_example.zip"
    val buildProductPath = Paths.get(dataPath, "Build", "Products")
    val currentPath = Paths.get("")

    Files.walk(currentPath)
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
