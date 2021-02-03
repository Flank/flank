package flank.scripts.ops.shell.buildexample.android

fun AndroidBuildConfiguration.runAndroidBuild() = takeIf { generate }?.let {
    buildBaseApk()
    buildBaseTestApk()
    buildDuplicatedNamesApks()
    buildMultiModulesApks()
    buildCucumberSampleApp()
}
