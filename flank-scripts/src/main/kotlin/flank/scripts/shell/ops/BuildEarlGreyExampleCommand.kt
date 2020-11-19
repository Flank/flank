package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.shell.utils.failIfWindows
import flank.scripts.shell.utils.iOSTestProjectsPath
import java.nio.file.Paths

object BuildEarlGreyExampleCommand : CliktCommand(name = "build_earl_grey_example", help = "Build ios earl grey example app with tests") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
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
            copy = copy ?: true
        ).generateIosTestArtifacts()
    }
}

const val EARL_GREY_EXAMPLE = "EarlGreyExample"
private const val EARL_GREY_EXAMPLE_TESTS = "EarlGreyExampleTests"
private const val EARL_GREY_EXAMPLE_SWIFT_TESTS = "EarlGreyExampleSwiftTests"
