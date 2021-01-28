package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.shell.ios.runFtlLocal

object RunFtlLocalCommand : CliktCommand(name = "iosRunFtlLocal", help = "Run ftl locally ios app") {

    private val deviceId by option(help = "Device id. Please take it from Xcode -> Window -> Devices and Simulators")
        .required()

    override fun run() {
        runFtlLocal(deviceId)
    }
}
