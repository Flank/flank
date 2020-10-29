package flank.scripts.shell.ops

import flank.scripts.shell.ios.createIosBuildCommand
import flank.scripts.shell.utils.flankFixturesIosTmpPath
import flank.scripts.shell.utils.iOSTestProjectsPath
import flank.scripts.shell.utils.pipe
import flank.scripts.utils.archive
import flank.scripts.utils.downloadCocoaPodsIfNeeded
import flank.scripts.utils.downloadXcPrettyIfNeeded
import flank.scripts.utils.installPods
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun IosBuildConfiguration.generateIos() {
    downloadCocoaPodsIfNeeded()
    installPods(Paths.get(projectPath))
    downloadXcPrettyIfNeeded()
    createDirectoryInFixture(directoryName = "objective_c")
    createDirectoryInFixture(directoryName = "swift")
    if (generate) buildEarlGreyExample()
}

private fun IosBuildConfiguration.createDirectoryInFixture(directoryName: String): Path =
    Files.createDirectories(Paths.get(flankFixturesIosTmpPath, projectName, directoryName))

private fun IosBuildConfiguration.buildEarlGreyExample() = Paths.get(projectPath, "Build")
    .runBuilds(this)
    .resolve("Products")
    .apply { renameXctestFiles().filterFilesToCopy().archiveProject(projectName).copyIosProductFiles(projectName) }
    .copyTestFiles(this)

private fun Path.runBuilds(configuration: IosBuildConfiguration) = apply {
    toFile().deleteRecursively()
    val parent = toFile().parent
    val workspace =
        if (configuration.useWorkspace) Paths.get(parent, configuration.workspaceName).toString()
        else ""

    val project = if (configuration.useWorkspace) ""
    else Paths.get(parent, "${configuration.projectName}.xcodeproj").toString()
    configuration.buildConfigurations.forEach {
        val buildCommand = createIosBuildCommand(
            parent,
            workspace,
            scheme = it.scheme,
            project,
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
    val appDirectory = Paths.get(productsDirectory, "Debug-iphoneos").toFile().findTestDirectories()
    appDirectory.forEach {
        it.walk().filter { it.isFile && it.extension == "" }.forEach { testFile ->
            configuration.copyTestFile(testFile)
        }
    }
}

private fun IosBuildConfiguration.copyTestFile(
    fileToCopy: File,
) =
    fileToCopy.copyTo(Paths.get(flankFixturesIosTmpPath, projectName, fileToCopy.name).toFile(), true)

private fun File.findTestDirectories() = walk().filter { it.isDirectory && it.name.endsWith(".xctest") }

data class IosBuildConfiguration(
    val projectPath: String = Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE).toString(),
    val projectName: String = EARL_GREY_EXAMPLE,
    val objcTestsName: String = EARL_GREY_EXAMPLE_TESTS,
    val swiftTestsName: String = EARL_GREY_EXAMPLE_SWIFT_TESTS,
    val buildConfigurations: List<IosTestBuildConfiguration> = emptyList(),
    val useWorkspace: Boolean = false,
    val generate: Boolean = true,
    val copy: Boolean = true
)

data class IosTestBuildConfiguration(val scheme: String, val outputDirectoryName: String)

private val IosBuildConfiguration.workspaceName
    get() = "$projectName.xcworkspace"

// private val IosBuildConfiguration.swiftAppDirectory
//    get() = "${projectName}Swift.app"

const val EARL_GREY_EXAMPLE = "EarlGreyExample"
const val EARL_GREY_EXAMPLE_TESTS = "EarlGreyExampleTests"
const val EARL_GREY_EXAMPLE_SWIFT_TESTS = "EarlGreyExampleSwiftTests"

const val FLANK_EXAMPLE = "FlankExample"
const val FLANK_EXAMPLE_TESTS = "FlankExampleTests"
const val FLANK_EXAMPLE_SECOND_TESTS = "FlankExampleSecondTests"
