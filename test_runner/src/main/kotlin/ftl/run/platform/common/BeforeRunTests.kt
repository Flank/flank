package ftl.run.platform.common

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcStorage
import ftl.gc.GcTesting
import ftl.gc.GcToolResults
import ftl.run.exception.FlankGeneralError
import ftl.util.StopWatch
import java.io.File

internal fun beforeRunTests(args: IArgs): Pair<StopWatch, String> {
    println("\nRunTests")
    assertMockUrl()

    val stopwatch = StopWatch().start()
    val runGcsPath = args.resultsDir

    // Avoid spamming the results/ dir with temporary files from running the test suite.
    if (FtlConstants.useMock)
        deleteMockResultDirOnShutDown(args, runGcsPath)

    if (args.useLocalResultDir()) {
        // Only one result is stored when using --local-result-dir
        // Delete any old results if they exist before storing new ones.
        deleteLocalResultDir(args)
    }

    return stopwatch to runGcsPath
}

private fun assertMockUrl() {
    if (!FtlConstants.useMock) return
    if (!GcTesting.get.rootUrl.contains(FtlConstants.localhost)) throw FlankGeneralError("expected localhost in GcTesting")
    if (!GcStorage.storageOptions.host.contains(FtlConstants.localhost)) throw FlankGeneralError("expected localhost in GcStorage")
    if (!GcToolResults.service.rootUrl.contains(FtlConstants.localhost)) throw FlankGeneralError("expected localhost in GcToolResults")
}

private fun deleteMockResultDirOnShutDown(args: IArgs, runGcsPath: String) {
    Runtime.getRuntime().addShutdownHook(Thread {
        File(args.localResultDir, runGcsPath).deleteRecursively()
    })
}

private fun deleteLocalResultDir(args: IArgs) {
    File(args.localResultDir).deleteRecursively()
}
