import ftl.gc.GcTestMatrix
import ftl.json.SavedMatrix
import ftl.util.asPrintableTable
import kotlinx.coroutines.runBlocking

// TODO remove before merge
fun main() {
    runBlocking {
        GcTestMatrix.refresh(
            testMatrixId = "matrix-1aspi39ikq5hy",
            projectId = "flank-open-source"
        ).let {
            SavedMatrix(it).apply {
                println("virtual: $billableVirtualMinutes")
                println("physical: $billablePhysicalMinutes")
            }
        }.run {
            println(asPrintableTable())
        }
    }
}
