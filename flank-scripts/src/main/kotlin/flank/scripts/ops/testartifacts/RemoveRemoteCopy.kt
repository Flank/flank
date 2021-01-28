package flank.scripts.ops.testartifacts

import flank.scripts.data.github.getRelease

fun Context.removeRemoteCopy() {
    print("* Removing remote copy for $branch - ")
    testArtifactsRepo().getRelease(branch)?.run {
        delete()
        println("OK")
    } ?: println("ABORTED (Release not found)")
}
