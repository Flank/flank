package ftl.run.platform.common

import flank.common.logLn
import ftl.adapter.google.GcStorage
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTesting
import ftl.gc.GcToolResults
import ftl.run.exception.FlankGeneralError
import ftl.util.StopWatch
import java.io.File

internal fun IArgs.beforeRunTests() = StopWatch().also { watch ->
    logLn("\nRunTests")
    assertMockUrl()

    watch.start()

    // Avoid spamming the results/ dir with temporary files from running the test suite.
    if (FtlConstants.useMock)
        deleteMockResultDirOnShutDown()

    // Only one result is stored when using --local-result-dir
    // Delete any old results if they exist before storing new ones.
    if (useLocalResultDir())
        deleteLocalResultDir()
}

private fun assertMockUrl() {
    if (!FtlConstants.useMock) return
    if (!GcTesting.get.rootUrl.contains(FtlConstants.localhost)) throw FlankGeneralError("expected localhost in GcTesting")
    if (!GcStorage.storageOptions.host.contains(FtlConstants.localhost)) throw FlankGeneralError("expected localhost in GcStorage")
    if (!GcToolResults.service.rootUrl.contains(FtlConstants.localhost)) throw FlankGeneralError("expected localhost in GcToolResults")
}

private fun IArgs.deleteMockResultDirOnShutDown() {
    File(localResultDir, resultsDir).deleteOnExit()
}

private fun IArgs.deleteLocalResultDir() {
    File(localResultDir).deleteRecursively()
}
