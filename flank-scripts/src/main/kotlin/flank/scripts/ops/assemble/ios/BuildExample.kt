package flank.scripts.ops.assemble.ios

import flank.common.archive
import flank.common.currentPath
import flank.common.iOSTestProjectsPath
import flank.scripts.ops.common.downloadXcPrettyIfNeeded
import flank.scripts.ops.common.installPodsIfNeeded
import flank.scripts.utils.failIfWindows
import flank.scripts.utils.pipe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

fun buildIosExample() {
    failIfWindows()
    downloadXcPrettyIfNeeded()
    buildExample()
}

private fun buildExample() {
    val projectPath: Path = Paths.get(iOSTestProjectsPath, EARLGREY_EXAMPLE)
    val buildPath: Path = Paths.get(projectPath.toString(), "build").apply {
        toFile().deleteRecursively()
    }

    val xcodeCommandSwiftTests = createXcodeBuildForTestingCommand(
        buildDir = buildPath.toString(),
        scheme = "EarlGreyExampleSwiftTests",
        workspace = Paths.get(projectPath.toString(), "EarlGreyExample.xcworkspace").toString(),
        useLegacyBuildSystem = true
    )

    installPodsIfNeeded(path = projectPath)
    xcodeCommandSwiftTests pipe "xcpretty"

    val xcodeCommandTests = createXcodeBuildForTestingCommand(
        buildDir = buildPath.toString(),
        scheme = "EarlGreyExampleTests",
        workspace = Paths.get(projectPath.toString(), "EarlGreyExample.xcworkspace").toString(),
        useLegacyBuildSystem = true
    )

    xcodeCommandTests pipe "xcpretty"

    copyExampleOutputFiles(buildPath.toString())
}

private fun copyExampleOutputFiles(buildPath: String) {
    val archiveFileName = "earlgrey_example.zip"
    val buildProductPath = Paths.get(buildPath, "Build", "Products")

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

private const val EARLGREY_EXAMPLE = "EarlGreyExample"
