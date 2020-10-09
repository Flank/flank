package flank.scripts.testartifacts.core

import flank.scripts.utils.unzip
import flank.scripts.utils.zip

fun Context.zipTestArtifacts() {
    projectRoot.testArtifacts(branch).run {
        zip(
            src = absoluteFile,
            dst = parentFile.resolve(ArtifactsArchive(branch).fullName)
        )
    }
}

fun Context.unzipTestArtifacts() {
    unzip(
        src = requireNotNull(latestArtifactsArchive()) {
            "Cannot find zip file for branch $branch in ${projectRoot.testArtifacts}"
        },
        dst = projectRoot.testArtifacts(branch).apply {
            if (exists() && isDirectory) {
                println("Deleting old $name")
                deleteRecursively()
            }
        }
    )
}
