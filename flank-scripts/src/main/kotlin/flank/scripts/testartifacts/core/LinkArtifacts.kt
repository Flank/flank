package flank.scripts.testartifacts.core

import flank.common.createSymbolicLink

fun Context.linkArtifacts() {
    print("* Creating link artifacts link for $branch - ")
    projectRoot.run {
        createSymbolicLink(
            target = testArtifacts(branch).absolutePath,
            link = resolve(FIXTURES_PATH).absolutePath
        )
    }
    println("OK")
}
