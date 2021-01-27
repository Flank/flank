package flank.scripts.ops.testartifacts

import flank.common.unzipFile
import flank.common.zip

fun Context.zipTestArtifacts() {
    projectRoot.testArtifacts(branch).run {
        zip(
            src = absoluteFile,
            dst = parentFile.resolve(ArtifactsArchive(branch).fullName)
        )
    }
}

fun Context.unzipTestArtifacts() {
    unzipFile(
        zipFileName = requireNotNull(latestArtifactsArchive()) {
            "Cannot find zip file for branch $branch in ${projectRoot.testArtifacts}"
        },
        unzipPath = projectRoot.testArtifacts(branch).apply {
            if (exists() && isDirectory) {
                println("Deleting old $name")
                deleteRecursively()
            }
        }.absolutePath
    )
}
