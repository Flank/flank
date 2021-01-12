package flank.scripts.testartifacts.core

import flank.common.linkFiles

fun Context.linkArtifacts() {
    print("* Creating link artifacts link for $branch - ")
    projectRoot.run {
        linkFiles(
            target = testArtifacts(branch).absolutePath,
            link = resolve(FIXTURES_PATH).absolutePath
        )
    }
    println("OK")
}
