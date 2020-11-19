package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.shell.utils.failIfWindows
import flank.scripts.shell.utils.iOSTestProjectsPath
import java.nio.file.Paths

object BuildMultiTestTargetsExample : CliktCommand(name = "build_ios_multi_test_targets_example", help = "Build ios multi test targets example app") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        failIfWindows()

        IosBuildConfiguration(
            projectPath = Paths.get(iOSTestProjectsPath, FLANK_IOS_MULTIPLE_TEST_TARGETS_EXAMPLE).toString(),
            projectName = FLANK_IOS_MULTIPLE_TEST_TARGETS_EXAMPLE,
            buildConfigurations = listOf(
                IosTestBuildConfiguration("AllTests", "AllTests"),
            ),
            useWorkspace = false,
            generate = generate ?: true,
            copy = copy ?: true
        ).generateIosTestArtifacts()
    }
}

private const val FLANK_IOS_MULTIPLE_TEST_TARGETS_EXAMPLE = "FlankMultiTestTargetsExample"
