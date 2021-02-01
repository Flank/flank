package flank.scripts.ops.shell.ios

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.common.currentPath
import flank.scripts.ops.shell.utils.failIfWindows
import flank.scripts.utils.runCommand
import java.nio.file.Path
import java.nio.file.Paths

object RunFtlLocalCommand : CliktCommand(name = "iosRunFtlLocal", help = "Run ftl locally ios app") {

    private val deviceId by option(help = "Device id. Please take it from Xcode -> Window -> Devices and Simulators")
        .required()

    override fun run() {
        failIfWindows()
        runFtlLocal(deviceId)
    }
}

private fun runFtlLocal(deviceId: String) {
    val dataPath: Path = Paths.get(currentPath.toString(), "dd_tmp", "Build", "Products")

    val xcodeCommand = "xcodebuild test-without-building " +
        " -xctestrun $dataPath/*.xctestrun " +
        "-derivedDataPath $dataPath " +
        "-destination 'id=$deviceId'"

    xcodeCommand.runCommand()
}
