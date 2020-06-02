package ftl.args

import ftl.config.FtlConstants
import ftl.util.FlankFatalError

fun CommonArgs.validate() {
    assertProjectId()
    assertMaxTestShards()
    assertShardTime()
    assertRepeatTests()
    assertSmartFlankGcsPath()
}

private fun CommonArgs.assertProjectId() {
    if (project.isEmpty()) throw FlankFatalError(
        "The project is not set. Define GOOGLE_CLOUD_PROJECT, set project in flank.yml\n" +
                "or save service account credential to ${FtlConstants.defaultCredentialPath}\n" +
                " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id"
    )
}

private fun CommonArgs.assertMaxTestShards() {
    if (
        maxTestShards !in IArgs.AVAILABLE_SHARD_COUNT_RANGE &&
        maxTestShards != -1
    ) throw FlankFatalError(
        "max-test-shards must be >= 1 and <= 50, or -1. But current is $maxTestShards"
    )
}

private fun CommonArgs.assertShardTime() {
    if (shardTime <= 0 && shardTime != -1) throw FlankFatalError(
        "shard-time must be >= 1 or -1"
    )
}

private fun CommonArgs.assertRepeatTests() {
    if (repeatTests < 1) throw FlankFatalError(
        "num-test-runs must be >= 1"
    )
}

private fun CommonArgs.assertSmartFlankGcsPath() = with(smartFlankGcsPath) {
    when {
        isEmpty() -> Unit

        startsWith(FtlConstants.GCS_PREFIX).not() -> throw FlankFatalError(
            "smart-flank-gcs-path must start with gs://"
        )

        count { it == '/' } <= 2 || endsWith(".xml").not() -> throw FlankFatalError(
            "smart-flank-gcs-path must be in the format gs://bucket/foo.xml"
        )
    }
}
