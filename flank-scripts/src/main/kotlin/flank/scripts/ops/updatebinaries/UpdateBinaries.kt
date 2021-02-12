package flank.scripts.ops.updatebinaries

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun updateBinaries() = runBlocking {
    listOf(
        launch { updateAtomic() },
        launch { updateLlvm() },
        launch { updateSwift() }
    ).joinAll()

    println("Binaries updated")
}
