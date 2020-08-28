package ftl.config.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.ArgsHelper
import ftl.args.yml.IYmlKeys
import ftl.config.Config
import ftl.config.FtlConstants
import ftl.shard.DEFAULT_CLASS_TEST_TIME_SEC
import ftl.shard.DEFAULT_TEST_TIME_SEC
import picocli.CommandLine

/** Flank specific parameters for both iOS and Android */
@CommandLine.Command
@JsonIgnoreProperties(ignoreUnknown = true)
data class CommonFlankConfig @JsonIgnore constructor(
    @field:JsonIgnore
    override val data: MutableMap<String, Any?>
) : Config {
    @set:CommandLine.Option(
        names = ["--max-test-shards"],
        description = ["The amount of matrices to split the tests across."]
    )
    @set:JsonProperty("max-test-shards")
    var maxTestShards: Int? by data

    @set:CommandLine.Option(
        names = ["--shard-time"],
        description = ["The max amount of seconds each shard should run."]
    )
    @set:JsonProperty("shard-time")
    var shardTime: Int? by data

    @set:CommandLine.Option(
        names = ["--num-test-runs"],
        description = ["The amount of times to run the test executions."]
    )
    @set:JsonProperty("num-test-runs")
    var repeatTests: Int? by data

    @set:CommandLine.Option(
        names = ["--smart-flank-gcs-path"],
        description = ["Google cloud storage path to save test timing data used by smart flank."]
    )
    @set:JsonProperty("smart-flank-gcs-path")
    var smartFlankGcsPath: String? by data

    @set:CommandLine.Option(
        names = ["--smart-flank-disable-upload"],
        description = ["Disables smart flank JUnit XML uploading. Useful for preventing timing data from being updated."]
    )
    @set:JsonProperty("smart-flank-disable-upload")
    var smartFlankDisableUpload: Boolean? by data

    @set:CommandLine.Option(
        names = ["--disable-sharding"],
        description = ["Disable sharding."]
    )
    @set:JsonProperty("disable-sharding")
    var disableSharding: Boolean? by data

    @set:CommandLine.Option(
        names = ["--test-targets-always-run"],
        split = ",",
        description = [
            "A list of one or more test methods to always run first in every shard."]
    )
    @set:JsonProperty("test-targets-always-run")
    var testTargetsAlwaysRun: List<String>? by data

    @set:CommandLine.Option(
        names = ["--files-to-download"],
        split = ",",
        description = ["A list of paths that will be downloaded from the resulting bucket " +
                "to the local results folder after the test is complete. These must be absolute paths " +
                "(for example, --files-to-download /images/tempDir1,/data/local/tmp/tempDir2). " +
                "Path names are restricted to the characters a-zA-Z0-9_-./+."]
    )
    @set:JsonProperty("files-to-download")
    var filesToDownload: List<String>? by data

    @set:CommandLine.Option(
        names = ["--project"],
        description = ["The Google Cloud Platform project name to use for this invocation. " +
                "If omitted, then the project from the service account credential is used"]
    )
    var project: String? by data

    @set:CommandLine.Option(
        names = ["--local-result-dir"],
        description = ["Saves test result to this local folder. Deleted before each run."]
    )
    @set:JsonProperty("local-result-dir")
    var localResultsDir: String? by data

    @set:CommandLine.Option(
        names = ["--run-timeout"],
        description = ["The max time this test run can execute before it is cancelled (default: unlimited)."]
    )
    @set:JsonProperty("run-timeout")
    var runTimeout: String? by data

    @set:CommandLine.Option(
        names = ["--full-junit-result"],
        description = ["Enable create additional local junit result on local storage with failure nodes on passed flaky tests."]
    )
    @set:JsonProperty("full-junit-result")
    var fullJUnitResult: Boolean? by data

    @set:CommandLine.Option(
        names = ["--ignore-failed-tests"],
        description = ["Terminate with exit code 0 when there are failed tests. " +
                "Useful for Fladle and other gradle plugins that don't expect the process to have a non-zero exit code. " +
                "The JUnit XML is used to determine failure. (default: false)"]
    )
    @set:JsonProperty("ignore-failed-tests")
    var ignoreFailedTests: Boolean? by data

    @set:CommandLine.Option(
        names = ["--keep-file-path"],
        description = ["Keeps the full path of downloaded files. " +
                "Required when file names are not unique."]
    )
    @set:JsonProperty("keep-file-path")
    var keepFilePath: Boolean? by data

    @set:CommandLine.Option(
        names = ["--output-style"],
        description = ["Output style of execution status. May be one of [verbose, multi, single]. " +
                "For runs with only one test execution the default value is 'verbose', in other cases " +
                "'multi' is used as the default. The output style 'multi' is not displayed correctly on consoles " +
                "which don't support ansi codes, to avoid corrupted output use `single` or `verbose`."]
    )
    @set:JsonProperty("output-style")
    var outputStyle: String? by data

    @set:CommandLine.Option(
        names = ["--disable-results-upload"],
        description = ["Disables flank results upload on gcloud storage."]
    )
    @set:JsonProperty("disable-results-upload")
    var disableResultsUpload: Boolean? by data

    @set:CommandLine.Option(
        names = ["--default-test-time"],
        description = ["Set default test time used for calculating shards."]
    )
    @set:JsonProperty("default-test-time")
    var defaultTestTime: Double? by data

    @set:JsonProperty("default-class-test-time")
    var defaultClassTestTime: Double? by data

    @set:CommandLine.Option(
        names = ["--use-average-test-time-for-new-tests"],
        description = ["Enable using average time from previous tests duration when using SmartShard and tests did not run before."]
    )
    @set:JsonProperty("use-average-test-time-for-new-tests")
    var useAverageTestTimeForNewTests: Boolean? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys {

        override val group = IYmlKeys.Group.FLANK

        override val keys = listOf(
            "max-test-shards",
            "shard-time",
            "num-test-runs",
            "smart-flank-gcs-path",
            "smart-flank-disable-upload",
            "disable-sharding",
            "test-targets-always-run",
            "files-to-download",
            "project",
            "run-timeout",
            "legacy-junit-result",
            "ignore-failed-tests",
            "keep-file-path",
            "output-style",
            "disable-results-upload",
            "full-junit-result",
            "local-result-dir"
        )

        const val defaultLocalResultsDir = "results"

        fun default() = CommonFlankConfig().apply {
            project = ArgsHelper.getDefaultProjectId() ?: ""
            maxTestShards = 1
            shardTime = -1
            repeatTests = 1
            smartFlankGcsPath = ""
            smartFlankDisableUpload = false
            testTargetsAlwaysRun = emptyList()
            filesToDownload = emptyList()
            disableSharding = false
            localResultsDir = defaultLocalResultsDir
            runTimeout = FtlConstants.runTimeout
            fullJUnitResult = false
            ignoreFailedTests = false
            keepFilePath = false
            outputStyle = null
            disableResultsUpload = false
            defaultTestTime = DEFAULT_TEST_TIME_SEC
            defaultClassTestTime = DEFAULT_CLASS_TEST_TIME_SEC
            useAverageTestTimeForNewTests = false
        }
    }
}
