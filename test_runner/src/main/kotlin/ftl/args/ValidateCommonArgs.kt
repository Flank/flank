package ftl.args

import flank.common.defaultCredentialPath
import flank.common.logLn
import ftl.api.RemoteStorage
import ftl.api.existRemoteStorage
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.reports.FullJUnitReport
import ftl.reports.JUnitReport
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError

fun CommonArgs.validate() {
    if (shouldValidateConfig) {
        assertProjectId()
        assertShardTime()
        assertRepeatTests()
        assertSmartFlankGcsPath()
        assertOrientationCorrectness()
        checkDisableSharding()
    }
}

private fun List<Device>.devicesWithMisspeltOrientations(availableOrientations: List<String>) =
    filter { it.orientation !in availableOrientations }

private fun CommonArgs.assertOrientationCorrectness() =
    devices.devicesWithMisspeltOrientations(listOf("portrait", "landscape", "default")).throwIfAnyMisspelt()

private fun List<Device>.throwIfAnyMisspelt() =
    if (isNotEmpty()) throw FlankGeneralError("Orientation misspelled or incorrect, found\n${joinToString(separator = "\n")} \nAborting.")
    else Unit

private fun CommonArgs.assertProjectId() {
    if (project.isBlank()) throw FlankConfigurationError(
        "The project is not set. Define GOOGLE_CLOUD_PROJECT, set project in flank.yml\n" +
            "or save service account credential to $defaultCredentialPath\n" +
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
        isBlank() -> Unit

        startsWith(FtlConstants.GCS_PREFIX).not() -> throw FlankConfigurationError(
            "smart-flank-gcs-path must start with gs://"
        )

        count { it == '/' } <= 2 || endsWith(".xml").not() -> throw FlankConfigurationError(
            "smart-flank-gcs-path must be in the format gs://bucket/foo.xml"
        )

        smartFlankDisableUpload.not() && contains("/${FullJUnitReport.fileName()}") && fullJUnitResult.not() -> throw FlankConfigurationError(
            "smart-flank-gcs-path is set with ${FullJUnitReport.fileName()} but in this run --full-junit-result is disabled, please set --full-junit-result flag"
        )

        smartFlankDisableUpload.not() && contains("/${JUnitReport.fileName()}") && fullJUnitResult -> throw FlankConfigurationError(
            "smart-flank-gcs-path is set with ${JUnitReport.fileName()} but in this run --full-junit-result enabled, please turn off --full-junit-result flag"
        )
    }
}

fun IArgs.checkResultsDirUnique() {
    if (useLegacyJUnitResult && existRemoteStorage(RemoteStorage.Dir(resultsBucket, resultsDir)))
        logLn("WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled\n")
}

fun IArgs.checkDisableSharding() {
    if (disableSharding && maxTestShards > 1)
        logLn("WARNING: disable-sharding enabled with max-test-shards = $maxTestShards, Flank will ignore max-test-shard and disable sharding.")
}
