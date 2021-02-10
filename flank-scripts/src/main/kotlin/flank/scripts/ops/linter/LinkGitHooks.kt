package flank.scripts.ops.linter

import flank.common.logLn
import flank.scripts.utils.runCommand

fun linkGitHooks(): Int = run {
    logLn("Linking Githooks.")
    "git config --local core.hooksPath .githooks/".runCommand()
}
