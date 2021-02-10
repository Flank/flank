package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.assemble.ios.runFtlLocal

object RunFtlLocalCommand : CliktCommand(
    name = "run_ftl_local",
    help = "Assemble iOS test plans application"
) {
    private val deviceId by option(
        help = "Device id. Please take it from Xcode -> Window -> Devices and Simulators"
    ).required()

    override fun run() {
        runFtlLocal(deviceId)
    }
}
