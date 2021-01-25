package flank.scripts.ops.contribution

import flank.scripts.utils.runCommand

fun linkGitHooks() = "git config --local core.hooksPath .githooks/".runCommand()
