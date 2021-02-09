package flank.scripts.ops.shell.buildexample.ios

import flank.common.iOSTestProjectsPath
import flank.scripts.ops.common.EARL_GREY_EXAMPLE
import flank.scripts.ops.common.EARL_GREY_EXAMPLE_SWIFT_TESTS
import flank.scripts.ops.common.EARL_GREY_EXAMPLE_TESTS
import flank.scripts.utils.failIfWindows
import java.nio.file.Paths

fun buildEarlGreyExample(generate: Boolean?, copy: Boolean?) {
    failIfWindows()
    IosBuildConfiguration(
        projectPath = Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE).toString(),
        projectName = EARL_GREY_EXAMPLE,
        buildConfigurations = listOf(
            IosTestBuildConfiguration(EARL_GREY_EXAMPLE_SWIFT_TESTS, "swift"),
            IosTestBuildConfiguration(EARL_GREY_EXAMPLE_TESTS, "objective_c")
        ),
        useWorkspace = true,
        generate = generate ?: true,
        copy = copy ?: true,
        copyXCTestFiles = copy ?: true,
        useLegacyBuildSystem = true
    ).generateIosTestArtifacts()
}
