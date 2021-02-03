package flank.scripts.ops.shell.buildexample.ios

import flank.common.iOSTestProjectsPath
import flank.scripts.utils.failIfWindows
import java.nio.file.Paths

fun buildIosGameLoopExampleCommand(generate: Boolean?, copy: Boolean?) {
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

private const val FLANK_GAME_LOOP_EXAMPLE = "FlankGameLoopExample"
