package flank.scripts.shell.utils

import flank.scripts.utils.runCommand

infix fun String.pipe(command: String) {
    "$this | $command".runCommand()
}
