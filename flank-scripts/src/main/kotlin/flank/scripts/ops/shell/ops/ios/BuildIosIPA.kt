package flank.scripts.ops.shell.ops.ios

import flank.common.flankFixturesIosTmpPath
import flank.scripts.ops.shell.ios.createXcodeArchiveCommand
import flank.scripts.ops.shell.ios.createXcodeExportArchiveCommand
import flank.scripts.utils.downloadCocoaPodsIfNeeded
import flank.scripts.utils.downloadXcPrettyIfNeeded
import flank.scripts.utils.installPodsIfNeeded
import flank.scripts.utils.pipe
import java.nio.file.Path
import java.nio.file.Paths

fun IosBuildConfiguration.generateIPA() {
    downloadCocoaPodsIfNeeded()
    installPodsIfNeeded(Paths.get(projectPath))
    downloadXcPrettyIfNeeded()
    if (generate) archiveProject()
}

private fun IosBuildConfiguration.archiveProject() = Paths.get(projectPath, "Build")
    .runXcodeArchiveBuilds(this)
    .resolve("ipa")
    .copyIPAFile(this)

private fun Path.runXcodeArchiveBuilds(configuration: IosBuildConfiguration) = apply {
    toFile().deleteRecursively()
    val parent = toFile().parent
    val workspace =
        if (configuration.useWorkspace) Paths.get(parent, configuration.workspaceName).toString()
        else ""

    val project = if (configuration.useWorkspace) ""
    else Paths.get(parent, "${configuration.projectName}.xcodeproj").toString()
    configuration.buildConfigurations.forEach {
        val archiveCommand = createXcodeArchiveCommand(
            "${toString()}/${it.scheme}.xcarchive",
            scheme = it.scheme,
            project = project,
            workspace = workspace
        )

        val exportArchiveCommand = createXcodeExportArchiveCommand(
            archivePath = "${toString()}/${it.scheme}.xcarchive",
            exportOptionsPlistPath = Paths.get(parent, "exportOptions.plist").toString(),
            exportPath = "${toString()}/ipa/"
        )
        archiveCommand pipe "xcpretty"
        exportArchiveCommand pipe "xcpretty"
    }
}

private fun Path.copyIPAFile(configuration: IosBuildConfiguration) {
    toFile().walk().filter { it.name.endsWith(".ipa") }.forEach {
        it.copyTo(Paths.get(flankFixturesIosTmpPath, configuration.projectName, it.name).toFile(), true)
    }
}
