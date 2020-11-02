package flank.scripts.shell.updatebinaries

import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object UpdateBinariesCommand : CliktCommand(name = "updateBinaries", help = "Update binaries used by Flank") {

    override fun run() {
        runBlocking {
            listOf(
                launch { updateAtomic() },
                launch { updateLlvm() },
                launch { updateSwift() }
            ).joinAll()

            println("Binaries updated")
        }
    }
}
