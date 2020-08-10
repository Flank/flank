package ftl.util

// ToolResults API step outcome values
object StepOutcome {
    // https://github.com/bootstraponline/gcloud_cli/blob/137d864acd5928baf25434cf59b0225c4d1f9319/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/toolresults_v1beta3.json#L610
    const val failure = "failure"
    const val flaky = "flaky"
    const val inconclusive = "inconclusive"
    const val skipped = "skipped"
    const val success = "success"
    const val unset = "unset"

    val notErrors = listOf(failure, flaky, skipped, success)

    val order = linkedSetOf(
        failure,
        flaky,
        success,
        inconclusive,
        skipped,
        unset
    ).reversed()
}
