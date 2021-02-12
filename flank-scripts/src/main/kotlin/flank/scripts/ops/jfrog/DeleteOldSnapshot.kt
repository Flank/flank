package flank.scripts.ops.jfrog

import flank.scripts.utils.runCommand

fun jFrogDeleteOldSnapshot(version: String) {
    "jfrog bt version-delete ${flankMaven(version)} --quiet".runCommand()
}
