package flank.scripts.cli.shell.buildexample

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.shell.buildexample.android.AndroidBuildConfiguration
import flank.scripts.ops.shell.buildexample.android.runAndroidBuild

object AndroidOpsCommand : CliktCommand(
    name = "android",
    help = "Build android apks with tests"
) {
    private val generate: Boolean by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    private val artifacts: List<String> by option().multiple()

    override fun run() {
        AndroidBuildConfiguration(artifacts, generate, copy).runAndroidBuild()
    }
}
