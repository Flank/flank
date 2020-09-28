package flank.scripts.testartifacts.core

import flank.scripts.utils.createSymbolicLink

fun Context.linkArtifacts() {
    projectRoot.run {
        createSymbolicLink(
            target = testArtifacts(branch).absolutePath,
            link = resolve(FIXTURES_PATH).absolutePath
        )
    }
}
