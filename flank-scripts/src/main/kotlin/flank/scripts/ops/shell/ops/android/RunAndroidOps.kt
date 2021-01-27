package flank.scripts.ops.shell.ops.android

fun AndroidBuildConfiguration.runAndroidBuild() = takeIf { generate }?.let {
    buildBaseApk()
    buildBaseTestApk()
    buildDuplicatedNamesApks()
    buildMultiModulesApks()
    buildCucumberSampleApp()
}
