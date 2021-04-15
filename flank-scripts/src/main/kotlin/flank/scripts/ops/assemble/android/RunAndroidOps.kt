package flank.scripts.ops.assemble.android

fun AndroidBuildConfiguration.runAndroidBuild() = takeIf { generate }?.let {
    buildBaseApk()
    buildBaseTestApk()
    buildDuplicatedNamesApks()
    buildMultiModulesApks()
    buildCucumberSampleApp()
    buildManyTestsApk()
    buildBenchmark()
}
