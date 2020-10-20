package flank.scripts.shell.updatebinaries

import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

object UpdateBinariesCommand : CliktCommand(name = "updateBinaries", help = "Update binaries used by Flank") {

    override fun run() {
        runBlocking {
            listOf(
                async { updateAtomic() },
                async { updateLlvm() },
                async { updateSwift() }
            ).awaitAll()

            println("Binaries updated")
        }
    }
}
