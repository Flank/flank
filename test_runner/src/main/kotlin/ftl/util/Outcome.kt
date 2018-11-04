package ftl.util

object Outcome {
    // https://github.com/bootstraponline/gcloud_cli/blob/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/toolresults_v1beta3.json#L755
    const val success = "success"
    const val failure = "failure"
    const val inconclusive = "inconclusive"
    const val skipped = "skipped"
    const val unset = "unset"
}
