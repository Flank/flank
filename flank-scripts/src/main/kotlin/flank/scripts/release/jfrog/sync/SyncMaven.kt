package flank.scripts.release.jfrog.sync

import flank.scripts.release.jfrog.flankMaven
import flank.scripts.utils.runCommand

fun jFrogSync(gitTag: String) {
    "jfrog bt mcs ${flankMaven(gitTag)}".runCommand(retryCount = 3)
}
