package ftl.run

import com.google.api.services.testing.model.IosDevice
import com.google.api.services.testing.model.TestMatrix
import ftl.config.YamlConfig
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.ios.IosCatalog
import ftl.json.MatrixMap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object IosTestRunner : GenericTestRunner {

    // https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
    override suspend fun runTests(config: YamlConfig): MatrixMap {
        val (stopwatch, runGcsPath) = beforeRunTests()

        val xcTestGcsPath = GcStorage.uploadXCTestZip(config, runGcsPath)

        val iosDevice = IosDevice()
                .setIosModelId(IosCatalog.model("iphone8"))
                .setIosVersionId(IosCatalog.version("11.2"))
                .setLocale("en_US") // iOS doesn't support other locales or orientations
                .setOrientation("portrait")

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = config.testRuns

        // iOS doesn't support shards
        println("  Running ${runCount}x")
        repeat(runCount) {
            jobs += async {
                GcIosTestMatrix.build(
                        iosDevice = iosDevice,
                        xcTestGcsPath = xcTestGcsPath,
                        runGcsPath = runGcsPath,
                        config = config).execute()
            }
        }

        return afterRunTests(jobs, runGcsPath, stopwatch, config)
    }
}
