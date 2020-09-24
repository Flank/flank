package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import flank.scripts.utils.unzip
import flank.scripts.utils.zip
import java.io.File

fun zipTestArtifacts(
    projectRoot: String,
    branchName: String = currentGitBranch()
) {
    projectRoot.testArtifactsPath(branchName).let {
        zip(
            src = it,
            dst = "$it-${System.currentTimeMillis()}.zip"
        )
    }
}

fun unzipTestArtifacts(
    projectRoot: String,
    branchName: String = currentGitBranch()
) {
    val testArtifactsRoot = "$projectRoot/test_artifacts"
    val regex = Regex("^$branchName-\\d*.zip")
    val zipFile = File(testArtifactsRoot)
        .listFiles { file -> regex.matches(file.name) }
        ?.maxBy { it.name.split("-").last().split(".").first() }
        ?: throw Exception("Cannot find zip file for branch $branchName in $projectRoot/test_artifacts")
    val dstDir = "$testArtifactsRoot/$branchName"
    File(dstDir).apply {
        if (exists() && isDirectory) {
            println("Deleting old $name")
            deleteRecursively()
        }
    }
    unzip(zipFile.absolutePath, dstDir)
}
