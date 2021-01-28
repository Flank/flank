package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.updatebinaries.updateBinaries

object UpdateBinariesCommand : CliktCommand(name = "updateBinaries", help = "Update binaries used by Flank") {

    override fun run() {
        updateBinaries()
    }
}
