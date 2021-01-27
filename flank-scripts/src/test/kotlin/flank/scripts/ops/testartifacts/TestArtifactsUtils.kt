package flank.scripts.ops.testartifacts

const val TEST_BRANCH_1 = "1-unit-test-artifacts-management"
const val TEST_BRANCH_2 = "2-unit-test-artifacts-management"
const val TEST_FILE_NAME = "test-file"
const val TEST_FILE_BODY = "Hello test artifacts!!!"

val testContext = Context(
    branch = TEST_BRANCH_1
)

fun Context.prepareTestDirectory() {
    projectRoot.testArtifacts(branch)
        .apply { mkdir() }
        .resolve(TEST_FILE_NAME)
        .writeText(TEST_FILE_BODY)
}

fun Context.removeTestDirectory() {
    projectRoot.testArtifacts(branch)
        .deleteRecursively()
}

fun Context.removeTestArchive() {
    projectRoot.testArtifacts.listFiles()?.forEach { file ->
        if (file.name.startsWith(branch) && file.extension == "zip")
            file.delete()
    }
}
