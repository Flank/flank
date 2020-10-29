package flank.scripts.shell.ios

fun createIosBuildCommand(buildDir: String, workspace: String, scheme: String, project: String = "") =
    "xcodebuild build-for-testing" +
        " -allowProvisioningUpdates" +
        (if (workspace.isBlank()) "" else " -workspace $workspace") +
        (if (project.isBlank()) "" else " -project $project") +
        " -scheme $scheme" +
        " -derivedDataPath $buildDir" +
        " -sdk iphoneos"
