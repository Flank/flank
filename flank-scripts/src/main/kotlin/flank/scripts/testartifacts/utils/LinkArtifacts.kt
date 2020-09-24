package flank.scripts.testartifacts.utils

import flank.scripts.utils.createSymbolicLink
import flank.scripts.utils.currentGitBranch

fun linkArtifacts(
    projectRoot: String,
    branchName: String = currentGitBranch()
) {
    createSymbolicLink(
        target = projectRoot.testArtifactsPath(branchName),
        link = projectRoot.fixturesPath
    )
}
