package flank.scripts.shell.utils

import flank.scripts.utils.isWindows
import kotlin.system.exitProcess

fun failIfWindows() {
    if (isWindows) {
        println("This script does not work on Windows")
        exitProcess(1)
    }
}
