package ftl.args

import ftl.config.Device
import ftl.config.FtlConstants
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError

fun CommonArgs.validate() {
    assertProjectId()
    assertShardTime()
    assertRepeatTests()
    assertSmartFlankGcsPath()
    assertOrientationCorrectness()
}

private fun List<Device>.devicesWithMispeltOrientations(availableOrientations: List<String>) =
    filter { it.orientation !in availableOrientations }

private fun CommonArgs.assertOrientationCorrectness() =
    devices.devicesWithMispeltOrientations(listOf("portrait", "landscape", "default")).throwIfAnyMisspelt()

private fun List<Device>.throwIfAnyMisspelt() =
    if (isNotEmpty()) throw FlankGeneralError("Orientation misspelled or incorrect, found\n${joinToString(separator = "\n")} \nAborting.")
    else Unit

private fun CommonArgs.assertProjectId() {
    if (project.isEmpty()) throw FlankConfigurationError(
        "The project is not set. Define GOOGLE_CLOUD_PROJECT, set project in flank.yml\n" +
                "or save service account credential to ${FtlConstants.defaultCredentialPath}\n" +
                " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id"
    )
}

private fun CommonArgs.assertShardTime() {
    if (shardTime <= 0 && shardTime != -1) throw FlankConfigurationError(
        "shard-time must be >= 1 or -1"
    )
}

private fun CommonArgs.assertRepeatTests() {
    if (repeatTests < 1) throw FlankConfigurationError(
        "num-test-runs must be >= 1"
    )
}

private fun CommonArgs.assertSmartFlankGcsPath() = with(smartFlankGcsPath) {
    when {
        isEmpty() -> Unit

        startsWith(FtlConstants.GCS_PREFIX).not() -> throw FlankConfigurationError(
            "smart-flank-gcs-path must start with gs://"
        )

        count { it == '/' } <= 2 || endsWith(".xml").not() -> throw FlankConfigurationError(
            "smart-flank-gcs-path must be in the format gs://bucket/foo.xml"
        )
    }
}
