package flank.scripts.ops.release.jfrog

import flank.scripts.utils.runCommand

fun jFrogSync(mavenTag: String) = "jfrog bt mcs ${flankMaven(mavenTag)}".runCommand(retryCount = 5)
