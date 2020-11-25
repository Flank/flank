package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.shell.utils.failIfWindows
import flank.scripts.shell.utils.iOSTestProjectsPath
import java.nio.file.Paths

object BuildGameLoopExampleCommand : CliktCommand(
    name = "build_ios_gameloop_example", 
    help = "Build ios game loop example app"
) {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        failIfWindows()

        IosBuildConfiguration(
            projectPath = Paths.get(iOSTestProjectsPath, FLANK_GAME_LOOP_EXAMPLE).toString(),
            projectName = FLANK_GAME_LOOP_EXAMPLE,
            buildConfigurations = listOf(
                IosTestBuildConfiguration(FLANK_GAME_LOOP_EXAMPLE, "tests"),
            ),
            useWorkspace = false,
            generate = generate ?: true,
            copy = copy ?: true
        ).generateIPA()
    }
}

private const val FLANK_GAME_LOOP_EXAMPLE = "FlankGameLoopExample"
