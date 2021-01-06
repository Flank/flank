package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.common.iOSTestProjectsPath
import flank.scripts.shell.utils.failIfWindows
import java.nio.file.Paths

object BuildFlankExampleCommand : CliktCommand(name = "build_flank_example", help = "Build ios flank example app with tests") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        failIfWindows()

        IosBuildConfiguration(
            projectPath = Paths.get(iOSTestProjectsPath, FLANK_EXAMPLE).toString(),
            projectName = FLANK_EXAMPLE,
            buildConfigurations = listOf(
                IosTestBuildConfiguration(FLANK_EXAMPLE, "tests"),
            ),
            useWorkspace = false,
            generate = generate ?: true,
            copy = copy ?: true
        ).generateIosTestArtifacts()
    }
}

private const val FLANK_EXAMPLE = "FlankExample"
