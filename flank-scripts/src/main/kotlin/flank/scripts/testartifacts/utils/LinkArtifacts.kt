package flank.scripts.testartifacts.utils

import flank.scripts.utils.createSymbolicLink
import flank.scripts.utils.currentGitBranch
import java.io.File

fun linkArtifacts(
    projectRoot: File,
    branchName: String = currentGitBranch()
) {
    createSymbolicLink(
        target = projectRoot.testArtifacts(branchName).absolutePath,
        link = projectRoot.name.fixturesPath
    )
}
