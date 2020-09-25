package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import flank.scripts.utils.unzip
import flank.scripts.utils.zip
import java.io.File

fun zipTestArtifacts(
    projectRoot: File,
    branchName: String = currentGitBranch()
) {
    projectRoot.testArtifacts(branchName).run {
        zip(
            src = absoluteFile,
            dst = parentFile.resolve(TestArtifactsZip(branchName).fullName)
        )
    }
}

fun unzipTestArtifacts(
    projectRoot: File,
    branch: String = currentGitBranch()
) {
    unzip(
        src = requireNotNull(latestArtifactsZip(projectRoot, branch)) {
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

data class TestArtifactsZip(
    val branch: String = currentGitBranch(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val fullName get() = "$branch-$timestamp.zip"
    val shortName get() = "$$timestamp.zip"

    override fun toString(): String = fullName
}

fun String.parseTestArtifactsZip(
    branch: String? = null
): TestArtifactsZip = this
    .removeSuffix(".zip")
    .reversed()
    .split("-", limit = 1)
    .run {
        TestArtifactsZip(
            branch = branch ?: last().reversed(),
            timestamp = first().reversed().toLong()
        )
    }

fun latestArtifactsZip(
    projectRoot: File = flankRoot(),
    branch: String = currentGitBranch()
): File? = artifactZipRegex(branch).let { regex ->
    projectRoot.testArtifacts.listFiles { file -> regex.matches(file.name) }
        ?.maxBy { zipNameTimestamp(it.name) }
}

fun artifactZipRegex(branch: String = currentGitBranch()) = Regex("^$branch-\\d*.zip")

fun zipNameTimestamp(name: String) = name.split("-").last().split(".").first()
