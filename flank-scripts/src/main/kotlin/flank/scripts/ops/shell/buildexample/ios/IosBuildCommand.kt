package flank.scripts.ops.shell.ios

fun createXcodeBuildForTestingCommand(
    buildDir: String,
    scheme: String,
    project: String = "",
    workspace: String = "",
    useLegacyBuildSystem: Boolean
) =
    "xcodebuild build-for-testing" +
        " -allowProvisioningUpdates" +
        (if (workspace.isBlank()) "" else " -workspace $workspace") +
        (if (project.isBlank()) "" else " -project $project") +
        " -scheme $scheme" +
        (if (useLegacyBuildSystem) " -UseModernBuildSystem=NO" else "") +
        " -derivedDataPath $buildDir" +
        " -sdk iphoneos"

fun createXcodeArchiveCommand(
    archivePath: String,
    scheme: String,
    project: String = "",
    workspace: String = ""
) =
    "xcodebuild" +
        " -allowProvisioningUpdates" +
        (if (workspace.isBlank()) "" else " -workspace $workspace") +
        (if (project.isBlank()) "" else " -project $project") +
        " -scheme $scheme" +
        " archive" +
        " -archivePath $archivePath"

fun createXcodeExportArchiveCommand(
    archivePath: String,
    exportOptionsPlistPath: String,
    exportPath: String = ""
) =
    "xcodebuild -exportArchive" +
        " -allowProvisioningUpdates" +
        " -archivePath $archivePath" +
        " -exportOptionsPlist $exportOptionsPlistPath" +
        " -exportPath $exportPath"
