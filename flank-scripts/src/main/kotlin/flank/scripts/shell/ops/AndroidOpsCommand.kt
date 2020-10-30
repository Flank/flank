package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option

object AndroidOpsCommand : CliktCommand(name = "android", help = "Build android apks with tests") {

    private val generate: Boolean by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean by option(help = "Copy output files to tmp").flag("-c", default = true)

    private val artifacts: List<String> by option().multiple()

    override fun run() {
        if (generate.not()) return
        AndroidBuildConfiguration(artifacts, generate, copy).run {
            buildBaseApk()
            buildBaseTestApk()
            buildDuplicatedNamesApks()
            buildMultiModulesApks()
            buildCucumberSampleApp()
        }
    }
}
