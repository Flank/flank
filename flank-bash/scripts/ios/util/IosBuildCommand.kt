fun createIosBuildCommand(buildDir: String, workSpace: String, scheme: String) =
    "xcodebuild build-for-testing " +
    "-allowProvisioningUpdates " +
    "-workspace $workSpace " +
    "-scheme $scheme " +
    "-derivedDataPath $buildDir " +
    "-sdk iphoneos"
