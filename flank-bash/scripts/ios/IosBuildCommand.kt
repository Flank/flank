fun createIosBuildCommand(buildDir: String, workspace: String, scheme: String) =
    "xcodebuild build-for-testing" +
    " -allowProvisioningUpdates" +
    " -workspace $workspace" +
    " -scheme $scheme" +
    " -derivedDataPath $buildDir" +
    " -sdk iphoneos"
