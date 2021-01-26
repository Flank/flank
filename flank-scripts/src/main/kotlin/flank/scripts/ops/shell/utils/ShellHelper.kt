package flank.scripts.ops.shell.utils

import flank.scripts.utils.isWindows
import flank.scripts.utils.runCommand

infix fun String.pipe(command: String) {
    if (isWindows) {
        listOf("cmd", "/C", "$this | $command").runCommand()
    } else {
        listOf("/bin/bash", "-c", "$this | $command").runCommand()
    }
}
