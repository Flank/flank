package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.shell.ops.android.AndroidBuildConfiguration
import flank.scripts.ops.shell.ops.android.runAndroidBuild
import flank.scripts.ops.shell.ops.ios.buildEarlGreyExample
import flank.scripts.ops.shell.ops.ios.buildIosFlankExample
import flank.scripts.ops.shell.ops.ios.buildIosGameLoopExampleCommand
import flank.scripts.ops.shell.ops.ios.buildTestPlansExample

object OpsCommand : CliktCommand(name = "ops", help = "Contains all ops command: android, ios, gp") {
    init {
        subcommands(
            AndroidOpsCommand,
            BuildEarlGreyExampleCommand,
            BuildGameLoopExampleCommand,
            BuildTestPlansExample,
            BuildFlankExampleCommand,
            IosOpsCommand,
            GoOpsCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}

object AndroidOpsCommand : CliktCommand(name = "android", help = "Build android apks with tests") {

    private val generate: Boolean by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean by option(help = "Copy output files to tmp").flag("-c", default = true)

    private val artifacts: List<String> by option().multiple()

    override fun run() {
        AndroidBuildConfiguration(artifacts, generate, copy).runAndroidBuild()
    }
}

object BuildEarlGreyExampleCommand :
    CliktCommand(name = "build_earl_grey_example", help = "Build ios earl grey example app with tests") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        buildEarlGreyExample(generate, copy)
    }
}

object BuildGameLoopExampleCommand : CliktCommand(
    name = "build_ios_gameloop_example",
    help = "Build ios game loop example app"
) {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        buildIosGameLoopExampleCommand(generate, copy)
    }
}

object BuildFlankExampleCommand :
    CliktCommand(name = "build_flank_example", help = "Build ios flank example app with tests") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        buildIosFlankExample(generate, copy)
    }
}

object BuildTestPlansExample :
    CliktCommand(name = "build_ios_testplans_example", help = "Build ios test plans example app") {

    private val generate: Boolean? by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        buildTestPlansExample(generate, copy)
    }
}
