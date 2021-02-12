package flank.scripts.ops.assemble.ios

import flank.common.archive
import flank.scripts.ops.common.downloadXcPrettyIfNeeded
import flank.scripts.utils.failIfWindows
import flank.scripts.utils.pipe
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

fun buildFtl() {
    failIfWindows()
    downloadXcPrettyIfNeeded()
    buildFtlExample()
}

private fun buildFtlExample() {
    val dataPath = Paths.get("", "dd_tmp").apply {
        toFile().deleteRecursively()
    }.toString()
    val xcodeCommand = createXcodeBuildForTestingCommand(
        buildDir = dataPath,
        scheme = "\"EarlGreyExampleSwiftTests\"",
        useLegacyBuildSystem = true
    )

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
