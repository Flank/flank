package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.shell.buildFlank
import flank.scripts.ops.shell.installXcPretty
import flank.scripts.ops.shell.ios.buildFtl
import flank.scripts.ops.shell.ios.buildIosExample
import flank.scripts.ops.shell.ios.createUniversalFrameworkFiles
import flank.scripts.ops.shell.ios.runFtlLocal
import flank.scripts.ops.shell.setupIosEnv
import flank.scripts.ops.shell.updatebinaries.UpdateBinariesCommand

object ShellCommand : CliktCommand(name = "shell", help = "Task for shell commands") {
    init {
        subcommands(
            FirebaseCommand,
            BuildExampleCommand,
            BuildFtlCommand,
            RunFtlLocalCommand,
            UniversalFrameworkCommand,
            OpsCommand,
            UpdateBinariesCommand,
            BuildFlankCommand,
            InstallXcPrettyCommand,
            SetupIosEnvCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}

object BuildExampleCommand : CliktCommand(name = "iosBuildExample", help = "Build example ios app") {
    override fun run() {
        buildIosExample()
    }
}

object BuildFtlCommand : CliktCommand(name = "iosBuildFtl", help = "Build ftl ios app") {
    override fun run() {
        buildFtl()
    }
}

object RunFtlLocalCommand : CliktCommand(name = "iosRunFtlLocal", help = "Run ftl locally ios app") {

    private val deviceId by option(help = "Device id. Please take it from Xcode -> Window -> Devices and Simulators")
        .required()

    override fun run() {
        runFtlLocal(deviceId)
    }
}

object UniversalFrameworkCommand : CliktCommand(name = "iosUniversalFramework", help = "Create Universal Framework") {
    override fun run() {
        createUniversalFrameworkFiles()
    }
}

object SetupIosEnvCommand : CliktCommand(name = "setup_ios_env", help = "Build ios app with tests") {
    override fun run() {
        setupIosEnv()
    }
}

object InstallXcPrettyCommand : CliktCommand(name = "install_xcpretty", help = "Build ios app with tests") {
    override fun run() {
        installXcPretty()
    }
}

object BuildFlankCommand : CliktCommand(name = "buildFlank", help = "Build Flank") {
    override fun run() {
        buildFlank()
    }
}
