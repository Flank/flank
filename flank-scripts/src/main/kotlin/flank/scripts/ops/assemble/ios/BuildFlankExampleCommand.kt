package flank.scripts.ops.assemble.ios

import flank.common.iOSTestProjectsPath
import flank.scripts.utils.failIfWindows
import java.nio.file.Paths

fun buildIosFlankExample(generate: Boolean?, copy: Boolean?) {
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

private const val FLANK_EXAMPLE = "FlankExample"
