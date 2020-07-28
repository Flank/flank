package flank.scripts.release.jfrog.delete

import flank.scripts.release.jfrog.flankMaven
import flank.scripts.utils.runCommand

fun jFrogDeleteOldSnapshot(version: String) {
    "jfrog bt version-delete ${flankMaven(version)} --quiet".runCommand()
}
