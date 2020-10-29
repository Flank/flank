package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.shell.utils.failIfWindows
import flank.scripts.shell.utils.iOSTestProjectsPath
import java.nio.file.Paths

object IosOpsCommand : CliktCommand(name = "ios", help = "Build ios app with tests") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        failIfWindows()

        IosBuildConfiguration(
            projectPath = Paths.get(iOSTestProjectsPath, FLANK_EXAMPLE).toString(),
            projectName = FLANK_EXAMPLE,
            objcTestsName = FLANK_EXAMPLE_TESTS,
            swiftTestsName = FLANK_EXAMPLE_SECOND_TESTS,
            buildConfigurations = listOf(
                IosTestBuildConfiguration(FLANK_EXAMPLE, "tests"),
            ),
            useWorkspace = false,
            generate = generate ?: true,
            copy = copy ?: true
        ).generateIos()

        IosBuildConfiguration(
            projectPath = Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE).toString(),
            projectName = EARL_GREY_EXAMPLE,
            objcTestsName = EARL_GREY_EXAMPLE_TESTS,
            swiftTestsName = EARL_GREY_EXAMPLE_SWIFT_TESTS,
            buildConfigurations = listOf(
                IosTestBuildConfiguration(EARL_GREY_EXAMPLE_SWIFT_TESTS, "swift"),
                IosTestBuildConfiguration(EARL_GREY_EXAMPLE_TESTS, "objective_c")
            ),
            useWorkspace = true,
            generate = generate ?: true,
            copy = copy ?: true
        ).generateIos()
    }
}
