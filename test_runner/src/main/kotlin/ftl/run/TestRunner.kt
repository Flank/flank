package ftl.run

import com.google.api.services.testing.model.TestDetails
import com.google.api.services.testing.model.TestMatrix
import com.google.cloud.storage.Storage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.*
import ftl.config.FtlConstants.indent
import ftl.config.FtlConstants.localResultsDir
import ftl.config.FtlConstants.localhost
import ftl.config.FtlConstants.matrixIdsFile
import ftl.gc.GcStorage
import ftl.gc.GcTestMatrix
import ftl.gc.GcTesting
import ftl.gc.GcToolResults
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.reports.util.ReportManager
import ftl.util.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object TestRunner {
    private val gson = GsonBuilder().setPrettyPrinting().create()!!

    val bitrise = System.getenv("BITRISE_IO") != null

    fun assertMockUrl() {
        if (!FtlConstants.useMock) return
        if (!GcTesting.get.rootUrl.contains(localhost)) throw RuntimeException("expected localhost in GcTesting")
        if (!GcStorage.storageOptions.host.contains(localhost)) throw RuntimeException("expected localhost in GcStorage")
        if (!GcToolResults.service.rootUrl.contains(localhost)) throw RuntimeException("expected localhost in GcToolResults")
    }

    private suspend fun runTests(args: IArgs): MatrixMap {
        return when (args) {
            is AndroidArgs -> AndroidTestRunner.runTests(args)
            is IosArgs -> IosTestRunner.runTests(args)
            else -> throw RuntimeException("Unknown config type")
        }
    }

    fun updateMatrixFile(matrixMap: MatrixMap): Path {
        val matrixIdsPath = Paths.get(FtlConstants.localResultsDir, matrixMap.runPath, FtlConstants.matrixIdsFile)
        matrixIdsPath.parent.toFile().mkdirs()
        Files.write(matrixIdsPath, gson.toJson(matrixMap.map).toByteArray())
        return matrixIdsPath
    }

    /** Refresh all in progress matrices in parallel **/
    private suspend fun refreshMatrices(matrixMap: MatrixMap, config: IArgs) {
        println("RefreshMatrices")

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val map = matrixMap.map
        var matrixCount = 0
        map.forEach { matrix ->
            // Only refresh unfinished
            if (MatrixState.inProgress(matrix.value.state)) {
                matrixCount += 1
                jobs += async { GcTestMatrix.refresh(matrix.key, config) }
            }
        }

        if (matrixCount != 0) {
            println(indent + "Refreshing ${matrixCount}x matrices")
        }

        var dirty = false
        jobs.forEach {
            val matrix = it.await()
            val matrixId = matrix.testMatrixId

            println(indent + "${matrix.state} $matrixId")

            if (map[matrixId]?.update(matrix) == true) dirty = true
        }

        if (dirty) {
            println(FtlConstants.indent + "Updating matrix file")
            updateMatrixFile(matrixMap)
        }
        println()
    }

    private fun lastGcsPath(): String {
        val resultsFile = Paths.get(FtlConstants.localResultsDir).toFile()
        val scheduledRuns = resultsFile.listFiles().filter { it.isDirectory }.map { it.name }

        val fileTimePairs = scheduledRuns.map {
            val dateTimePair = it.split('.')[0].split('_')
            val date = LocalDate.parse(dateTimePair[0])
            val time = LocalTime.parse(dateTimePair[1])
            Pair(it, LocalDateTime.of(date, time))
        }.sortedByDescending { it.second }
        return fileTimePairs.first().first
    }

    /** Reads in the last matrices from the localResultsDir folder **/
    private fun lastMatrices(): MatrixMap {
        val lastRun = lastGcsPath()
        println("Loading run $lastRun")
        return matrixPathToObj(lastRun)
    }

    /** Creates MatrixMap from matrix_ids.json file */
    fun matrixPathToObj(path: String): MatrixMap {
        var filePath = Paths.get(path, matrixIdsFile).toFile()
        if (!filePath.exists()) {
            filePath = Paths.get(localResultsDir, path, matrixIdsFile).toFile()
        }
        val json = filePath.readText()

        val listOfSavedMatrix = object : TypeToken<MutableMap<String, SavedMatrix>>() {}.type
        val map: MutableMap<String, SavedMatrix> = gson.fromJson(json, listOfSavedMatrix)

        return MatrixMap(map, path)
    }

    /** fetch test_result_0.xml & *.png **/
    private fun fetchArtifacts(matrixMap: MatrixMap) {
        println("FetchArtifacts")
        if (FtlConstants.useMock) return
        val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)

        var dirty = false
        val filtered = matrixMap.map.values.filter {
            val finished = it.state == MatrixState.FINISHED
            val notDownloaded = !it.downloaded
            val failure = it.outcome == Outcome.failure
            finished && notDownloaded && failure
        }

        print(indent)
        filtered.forEach { matrix ->
            val prefix = Storage.BlobListOption.prefix(matrix.gcsPathWithoutRootBucket)
            val result = GcStorage.storage.list(matrix.gcsRootBucket, prefix, fields)

            result.iterateAll().forEach { blob ->
                val blobPath = blob.blobId.name
                if (
                        blobPath.matches(ArtifactRegex.testResultRgx) ||
                        blobPath.matches(ArtifactRegex.screenshotRgx)
                ) {
                    val downloadFile = Paths.get(FtlConstants.localResultsDir, blobPath)
                    print(".")
                    if (!downloadFile.toFile().exists()) {
                        downloadFile.parent.toFile().mkdirs()
                        blob.downloadTo(downloadFile)
                    }
                }
            }

            dirty = true
            matrix.downloaded = true
        }
        println()

        if (dirty) {
            println(FtlConstants.indent + "Updating matrix file")
            updateMatrixFile(matrixMap)
            println()
        }
    } // fun

    /** Synchronously poll all matrix ids until they complete. Returns true if test run passed. **/
    private fun pollMatrices(matrices: MatrixMap, config: IArgs) {
        println("PollMatrices")
        val map = matrices.map
        val poll = matrices.map.values.filter {
            MatrixState.inProgress(it.state)
        }

        val stopwatch = StopWatch().start()
        poll.forEach {
            val matrixId = it.matrixId
            val completedMatrix = pollMatrix(matrixId, stopwatch, config)

            map[matrixId]?.update(completedMatrix)
        }
        println()

        updateMatrixFile(matrices)
    }

    // Used for when the matrix has exactly one test. Polls for detailed progress
    //
    // Port of MonitorTestExecutionProgress
    // gcloud-cli/googlecloudsdk/api_lib/firebase/test/matrix_ops.py
    private fun pollMatrix(matrixId: String, stopwatch: StopWatch, config: IArgs): TestMatrix {
        var lastState = ""
        var lastError = ""
        var progress = listOf<String>()
        var lastProgressLen = 0
        var refreshedMatrix: TestMatrix

        fun puts(msg: String) {
            val timestamp = stopwatch.check(indent = true)
            println("${FtlConstants.indent}$timestamp $matrixId $msg")
        }

        while (true) {
            refreshedMatrix = GcTestMatrix.refresh(matrixId, config)

            val firstTestStatus = refreshedMatrix.testExecutions.first()

            val details: TestDetails? = firstTestStatus.testDetails
            if (details != null) {
                // Error message is never reset. Track last error to only print new messages.
                val errorMessage = details.errorMessage
                if (
                        errorMessage != null &&
                        errorMessage != lastError
                ) {
                    // Note: After an error (infrastructure failure), FTL will retry 3x
                    lastError = errorMessage
                    puts("Error: $lastError")
                }
                progress = details.progressMessages ?: progress
            }

            if (MatrixState.completed(refreshedMatrix.state)) {
                break
            }

            // Progress contains all messages. only print new updates
            for (msg in progress.listIterator(lastProgressLen)) {
                puts(msg)
                // There may be significant time lag between 'Done' and the matrix actually finishing.
                if (msg.contains("Done. Test time=")) {
                    puts("Waiting for post-processing service to finish")
                }
            }
            lastProgressLen = progress.size

            if (firstTestStatus.state != lastState) {
                lastState = firstTestStatus.state
                puts(lastState)
            }

            // GetTestMatrix is not designed to handle many requests per second.
            // Sleep 15s to avoid overloading the system.
            Utils.sleep(15)
        }

        // Print final matrix state with timestamp. May be many minutes after the 'Done.' progress message.
        puts(refreshedMatrix.state)
        return refreshedMatrix
    }

    // used to update results from an async run
    suspend fun refreshLastRun(config: IArgs) {
        val matrixMap = lastMatrices()

        refreshMatrices(matrixMap, config)
        fetchArtifacts(matrixMap)
        // Must generate reports *after* fetching xml artifacts since reports require xml
        ReportManager.generate(matrixMap)
    }

    suspend fun newRun(config: IArgs) {
        println(config)
        val matrixMap = runTests(config)

        if (!config.async) {
            pollMatrices(matrixMap, config)
            fetchArtifacts(matrixMap)

            val testsSuccessful = ReportManager.generate(matrixMap)
            if (!testsSuccessful) System.exit(1)
        }
    }
}
