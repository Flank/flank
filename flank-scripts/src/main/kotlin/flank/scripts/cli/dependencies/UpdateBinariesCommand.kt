package flank.scripts.cli.dependencies

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.updatebinaries.updateBinaries

object UpdateBinariesCommand : CliktCommand(
    name = "update_binaries",
    help = "Update binaries used by Flank"
) {
    override fun run() {
        updateBinaries()
    }
}
