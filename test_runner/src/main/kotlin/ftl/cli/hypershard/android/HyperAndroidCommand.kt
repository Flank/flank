package ftl.cli.hypershard.android

import com.dropbox.mobile.hypershard.ClassAnnotationValue
import com.dropbox.mobile.hypershard.RealHyperShard
import ftl.util.FlankFatalError
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.lang.Runnable
import java.nio.file.Paths

private const val RESULT_FILE = "hypershard-android-results.txt"

@Command(
    name = "android",
    sortOptions = true,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Run hypershard-android against test classes"],
    description = ["Hypershard is a fast and simple test collector that uses the Kotlin and Java ASTs. " +
            "Flank will save to file full qualified test names found in dir(s)" +
            " that can be used later as input for --test-targets option."
    ],
    usageHelpAutoWidth = true
)
class HyperAndroidCommand : Runnable {
    // Hypershard output: any.path.to.TestClass.test1
    // But test-targets requires: any.path.to.TestClass#test1
    private val regex = """\.(?=[^.]*$)""".toRegex()

    override fun run() {
        runBlocking {
            val annotationValue = annotationName?.let { ClassAnnotationValue.Present(it) } ?: ClassAnnotationValue.Empty
            val resultPath = Paths.get(outputPath, RESULT_FILE).toAbsolutePath().toString()
            val hyperShard = RealHyperShard(annotationValue, dirs ?: throw FlankFatalError("Missing --dirs parameter"))

            val spinner = launchSpinner()
            val tests = hyperShard.gatherTests().map { it.replace(regex, "#") }

            File(resultPath).bufferedWriter().use { out ->
                tests.forEach {
                    out.write(it)
                    out.newLine()
                }
                spinner.cancel()
                println("\rDone!")
                println("Results file writen to $resultPath")
            }
        }
    }

    @Option(
        names = ["--annotation-name"],
        description = ["Class annotation name to process. For example, if this was set to 'UiTest',then Hypershard " +
                "will only process classes annotated with @UiTest."]
    )
    var annotationName: String? = null

    @Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    @Option(
        names = ["--dirs"],
        description = ["Dir(s) to process. The location of the test classes to parse"],
        split = ","
    )
    var dirs: List<String>? = null

    @Option(
        names = ["-o", "--output"],
        description = ["Location where result file will be written"]
    )
    var outputPath: String = ""
}

private fun <T> Sequence<T>.repeat() = sequence {
    while (true) {
        yieldAll(this@repeat)
    }
}

private fun CoroutineScope.launchSpinner() = launch(Dispatchers.IO) {
    print("Working:   ")
    listOf("\u28f7", "\u28ef", "\u28df", "\u287f", "\u28bf", "\u28fb", "\u28fd", "\u28fe")
        .asSequence()
        .repeat()
        .forEach {
            print("\b\b$it ")
            delay(250)
        }
}
