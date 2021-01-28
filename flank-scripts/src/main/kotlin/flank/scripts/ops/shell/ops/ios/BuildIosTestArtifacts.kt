package flank.scripts.ops.shell.ops.ios

import flank.common.archive
import flank.common.flankFixturesIosTmpPath
import flank.scripts.ops.shell.ios.createXcodeBuildForTestingCommand
import flank.scripts.utils.downloadCocoaPodsIfNeeded
import flank.scripts.utils.downloadXcPrettyIfNeeded
import flank.scripts.utils.installPodsIfNeeded
import flank.scripts.utils.pipe
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun IosBuildConfiguration.generateIosTestArtifacts() {
    downloadCocoaPodsIfNeeded()
    installPodsIfNeeded(Paths.get(projectPath))
    downloadXcPrettyIfNeeded()
    if (generate) buildProject()
}

private fun IosBuildConfiguration.buildProject() = Paths.get(projectPath, "Build")
    .runBuilds(this)
    .resolve("Build")
    .resolve("Products")
    .apply { renameXctestFiles().filterFilesToCopy().archiveProject(projectName).copyIosProductFiles(projectName) }
    .let {
        if (this.copyXCTestFiles) it.copyTestFiles(this)
    }

private fun Path.runBuilds(configuration: IosBuildConfiguration) = apply {
    toFile().deleteRecursively()
    val parent = toFile().parent
    val workspace =
        if (configuration.useWorkspace) Paths.get(parent, configuration.workspaceName).toString()
        else ""

    val project = if (configuration.useWorkspace) ""
    else Paths.get(parent, "${configuration.projectName}.xcodeproj").toString()
    configuration.buildConfigurations.forEach {
        val buildCommand = createXcodeBuildForTestingCommand(
            toString(),
            scheme = it.scheme,
            project = project,
            workspace = workspace,
            useLegacyBuildSystem = configuration.useLegacyBuildSystem
        )
        buildCommand pipe "xcpretty"
    }
}

private fun Path.renameXctestFiles() = apply {
    toFile().walk().filter { it.extension == "xctestrun" }.forEach {
        it.reduceTestFileName()
    }
}

private fun Sequence<File>.archiveProject(projectName: String) = also {
    it.toList().archive("$projectName.zip", File(flankFixturesIosTmpPath, projectName))
}

private fun File.reduceTestFileName() =
    renameTo(toPath().parent.resolve(name.reduceTestFileName()).toFile())

private fun String.reduceTestFileName() =
    "_.*xctestrun".toRegex().replace(this, ".xctestrun")

private fun Path.filterFilesToCopy() =
    toFile().walk().filter { it.nameWithoutExtension.endsWith("-iphoneos") || it.extension == "xctestrun" }

private fun Sequence<File>.copyIosProductFiles(projectName: String) = forEach {
    if (it.isDirectory) it.copyRecursively(Paths.get(flankFixturesIosTmpPath, projectName, it.name).toFile(), overwrite = true)
    else it.copyTo(Paths.get(flankFixturesIosTmpPath, projectName, it.name).toFile(), overwrite = true)
}

private fun Path.copyTestFiles(configuration: IosBuildConfiguration) = toString().takeIf { configuration.copy }?.let { productsDirectory ->
    val appDirectory = Paths.get(productsDirectory, "Debug-iphoneos").toFile().findXCTestDirectories()
    appDirectory.forEach {
        it.walk().filter { it.isFile && it.extension == "" && !it.name.contains("CodeResources") }.forEach { testFile ->
            configuration.copyTestFile(testFile)
        }
    }
}

private fun IosBuildConfiguration.copyTestFile(
    fileToCopy: File,
) =
    fileToCopy.copyTo(Paths.get(flankFixturesIosTmpPath, projectName, fileToCopy.name).toFile(), true)

private fun File.findXCTestDirectories() = walk().filter { it.isDirectory && it.name.endsWith(".xctest") }

data class IosBuildConfiguration(
    val projectPath: String,
    val projectName: String,
    val buildConfigurations: List<IosTestBuildConfiguration>,
    val useWorkspace: Boolean = false,
    val generate: Boolean = true,
    val copy: Boolean = true,
    val copyXCTestFiles: Boolean = false,
    val useLegacyBuildSystem: Boolean = false
)

data class IosTestBuildConfiguration(val scheme: String, val outputDirectoryName: String)

val IosBuildConfiguration.workspaceName
    get() = "$projectName.xcworkspace"
