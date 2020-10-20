package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.ios.createIosBuildCommand
import flank.scripts.shell.utils.failIfWindows
import flank.scripts.shell.utils.flankFixturesTmpPath
import flank.scripts.shell.utils.iOSTestProjectsPath
import flank.scripts.shell.utils.pipe
import flank.scripts.utils.downloadCocoaPodsIfNeeded
import flank.scripts.utils.downloadXcPrettyIfNeeded
import flank.scripts.utils.installPods
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

enum class TestType {
    SWIFT,
    OBJECTIVE_C
}

object IosOpsCommand : CliktCommand(name = "ios", help = "Build ios app with tests") {
    override fun run() {
        failIfWindows()
        generateIos()
    }

    private fun generateIos() {
        downloadCocoaPodsIfNeeded()
        installPods(Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE))
        downloadXcPrettyIfNeeded()
        createDirectoryInFixture(directoryName = "objc")
        createDirectoryInFixture(directoryName = "swift")
        buildEarlGreyExample()
    }

    private fun createDirectoryInFixture(directoryName: String): Path =
        Files.createDirectories(Paths.get(flankFixturesTmpPath, directoryName))

    private fun buildEarlGreyExample() = Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE, "Build")
        .runBuilds()
        .resolve("Products")
        .apply { filterFilesToCopy().copyIosProductFiles() }
        .copyTestFiles()

    private fun Path.runBuilds() = apply {
        toFile().deleteRecursively()
        val parent = toFile().parent
        val swiftCommand = createIosBuildCommand(
            parent,
            Paths.get(parent, "EarlGreyExample.xcworkspace").toString(),
            scheme = EARL_GREY_EXAMPLE_TESTS
        )
        swiftCommand pipe "xcpretty"

        val objcCommand = createIosBuildCommand(
            parent,
            Paths.get(parent, "EarlGreyExample.xcworkspace").toString(),
            scheme = EARL_GREY_EXAMPLE_SWIFT_TESTS
        )
        objcCommand pipe "xcpretty"
    }

    private fun Path.filterFilesToCopy() =
        toFile().walk().filter { it.nameWithoutExtension.endsWith("-iphoneos") || it.extension == "xctestrun" }

    private fun Sequence<File>.copyIosProductFiles() = forEach {
        if (it.isDirectory) it.copyRecursively(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
        else it.copyTo(Paths.get(flankFixturesTmpPath, it.name).toFile(), overwrite = true)
    }

    private fun Path.copyTestFiles() = toString().let { productsDirectory ->
        val pluginsDirectory = arrayOf("Debug-iphoneos", "EarlGreyExampleSwift.app", "PlugIns")
        copyTestFile(productsDirectory, pluginsDirectory, EARL_GREY_EXAMPLE_TESTS, TestType.OBJECTIVE_C)
        copyTestFile(productsDirectory, pluginsDirectory, EARL_GREY_EXAMPLE_SWIFT_TESTS, TestType.SWIFT)
    }

    private fun copyTestFile(
        productsDirectory: String,
        pluginsDirectories: Array<String>,
        name: String,
        type: TestType
    ) = Files.copy(
        Paths.get(productsDirectory, *pluginsDirectories, "$name.xctest", name),
        Paths.get(flankFixturesTmpPath, type.toString().toLowerCase(), name),
        StandardCopyOption.REPLACE_EXISTING
    )
}

private const val EARL_GREY_EXAMPLE = "EarlGreyExample"
private const val EARL_GREY_EXAMPLE_TESTS = "EarlGreyExampleTests"
private const val EARL_GREY_EXAMPLE_SWIFT_TESTS = "EarlGreyExampleSwiftTests"
