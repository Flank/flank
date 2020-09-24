package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import java.io.File

fun prepareTestArtifacts(
    projectRoot: File = flankRoot(),
    src: String = "master",
    dst: String = currentGitBranch()
) {
    projectRoot.resolve("test_artifacts").apply {
        resolve(dst).let { dstDir ->
            if (!dstDir.exists()) resolve(src).copyRecursively(dstDir)
            else throw Exception("${dstDir.absolutePath} already exist.")
        }
    }
}
