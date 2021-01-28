package flank.scripts.ops.shell.buildexample.ios

import flank.common.iOSTestProjectsPath
import flank.scripts.utils.failIfWindows
import java.nio.file.Paths

fun buildTestPlansExample(generate: Boolean?, copy: Boolean?) {
    failIfWindows()

    IosBuildConfiguration(
        projectPath = Paths.get(iOSTestProjectsPath, FLANK_TEST_PLANS_EXAMPLE).toString(),
        projectName = FLANK_TEST_PLANS_EXAMPLE,
        buildConfigurations = listOf(
            IosTestBuildConfiguration("AllTests", "AllTests"),
        ),
        useWorkspace = false,
        generate = generate ?: true,
        copy = copy ?: true
    ).generateIosTestArtifacts()
}

private const val FLANK_TEST_PLANS_EXAMPLE = "FlankTestPlansExample"
