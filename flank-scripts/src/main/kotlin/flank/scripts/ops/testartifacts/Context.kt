package flank.scripts.ops.testartifacts

import flank.scripts.utils.currentGitBranch
import java.io.File
import java.io.PrintStream

data class Context(
    val projectRoot: File = flankRoot(),
    val branch: String = currentGitBranch(),
    val out: PrintStream = System.out
) {

    fun print(any: Any) {
        out.print(any)
        out.flush()
    }

    fun println(any: Any? = null) {
        any?.let(out::println)
    }
}
