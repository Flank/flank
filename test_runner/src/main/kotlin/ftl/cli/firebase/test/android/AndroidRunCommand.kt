package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.config.FtlConstants
import ftl.run.TestRunner
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Paths

@Command(
    name = "run",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Run tests on Firebase Test Lab"],
    description = ["""Uploads the app and test apk to GCS.
Runs the espresso tests using orchestrator.
Configuration is read from flank.yml
"""]
)
class AndroidRunCommand : Runnable {

    override fun run() {
        val config = AndroidArgs.load(Paths.get(configPath), this)
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultAndroidConfig

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    @Option(names = ["--app"], description = ["""The path to the application binary file.
        |The path may be in the local filesystem or in Google Cloud Storage using gs:// notation."""])
    var app: String? = null

    @Option(names = ["--test"], description = ["""The path to the binary file containing instrumentation tests.
        |The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."""])
    var test: String? = null

    @Option(names = ["--test-targets"], description = ["""A list of one or more test target filters to apply
         (default: run all test targets). Each target filter must be fully qualified with the package name, class name,
          or test annotation desired. Any test filter supported by am instrument -e … is supported.
          See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more
          information."""])
    var testTargets: List<String>? = null
}
