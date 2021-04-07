package flank.scripts.ops.assemble.android

import flank.common.androidTestProjectsPath
import flank.common.flankFixturesTmpPath
import flank.scripts.utils.createGradleCommand
import flank.scripts.utils.runCommand
import java.nio.file.Paths

// TODO Design common abstraction for building apks and coping artifacts, add java doc.

private const val BENCHMARK = "benchmark"

fun AndroidBuildConfiguration.buildBenchmark() {
    if (artifacts.canExecute(BENCHMARK).not()) return
    createGradleCommand(
        workingDir = androidTestProjectsPath,
        options = listOf(
            "-p",
            androidTestProjectsPath,
            "$BENCHMARK:assembleDebugAndroidTest"
        )
    ).runCommand()

    if (copy) copyApks()
}

private fun copyApks() {
    val outputDir = Paths.get(flankFixturesTmpPath, "apk", BENCHMARK).toString()
    Paths.get(androidTestProjectsPath, BENCHMARK).toFile().findApks().copyApksToPath(outputDir)
}

