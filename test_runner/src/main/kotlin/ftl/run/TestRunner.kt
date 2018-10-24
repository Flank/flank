package ftl.run

import com.google.api.services.testing.model.TestDetails
import com.google.api.services.testing.model.TestMatrix
import com.google.cloud.storage.Storage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.defaultAndroidConfig
import ftl.config.FtlConstants.defaultIosConfig
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
import ftl.util.ArtifactRegex
import ftl.util.MatrixState
import ftl.util.StopWatch
import ftl.util.Utils
import ftl.util.Utils.fatalError
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object TestRunner {
    private val gson = GsonBuilder().setPrettyPrinting().create()!!

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

    fun saveConfigFile(matrixRunPath: String, config: IArgs): Path? {
        val configFilePath =
            Paths.get(FtlConstants.localResultsDir, matrixRunPath, FtlConstants.configFileName(config))
        configFilePath.parent.toFile().mkdirs()
        Files.write(configFilePath, config.data.toByteArray())
        return configFilePath
    }

    /** Refresh all in progress matrices in parallel **/
    private suspend fun refreshMatrices(matrixMap: MatrixMap, config: IArgs) = coroutineScope {
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
        jobs.awaitAll().forEach { matrix ->
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

    /** Cancel all in progress matrices in parallel **/
    private fun cancelMatrices(matrixMap: MatrixMap, config: IArgs) {
        println("CancelMatrices")

        val map = matrixMap.map
        var matrixCount = 0

        runBlocking {
            map.forEach { matrix ->
                // Only cancel unfinished
                if (MatrixState.inProgress(matrix.value.state)) {
                    matrixCount += 1
                    launch { GcTestMatrix.cancel(matrix.key, config) }
                }
            }
        }

        if (matrixCount == 0) {
            println(indent + "No matrices to cancel")
        } else {
            println(indent + "Cancelling ${matrixCount}x matrices")
        }

        println()
    }

    private fun lastGcsPath(): String? {
        val resultsFile = Paths.get(FtlConstants.localResultsDir).toFile()
        if (!resultsFile.exists()) return null
        val scheduledRuns = resultsFile.listFiles().filter { it.isDirectory }.map { it.name }
        if (scheduledRuns.isEmpty()) return null

        val fileTimePairs = scheduledRuns.map {
            val dateTimePair = it.split('.')[0].split('_')
            val date = LocalDate.parse(dateTimePair[0])
            val time = LocalTime.parse(dateTimePair[1])
            Pair(it, LocalDateTime.of(date, time))
        }.sortedByDescending { it.second }
        return fileTimePairs.first().first
    }

    /** Reads in the last matrices from the localResultsDir folder **/
    private fun lastConfig(): IArgs {
        val lastRun = lastGcsPath()

        if (lastRun == null) {
            fatalError("no runs found in results/ folder")
        }

        val iosConfig = Paths.get(localResultsDir, lastRun, defaultIosConfig)
        val androidConfig = Paths.get(localResultsDir, lastRun, defaultAndroidConfig)

        when {
            iosConfig.toFile().exists() -> return IosArgs.load(iosConfig)
            androidConfig.toFile().exists() -> return AndroidArgs.load(androidConfig)
            else -> fatalError("No config file found in the last run folder: $lastRun")
        }

        throw RuntimeException("should not happen")
    }

    /** Reads in the last matrices from the localResultsDir folder **/
    private fun lastMatrices(): MatrixMap {
        val lastRun = lastGcsPath()

        if (lastRun == null) {
            fatalError("no runs found in results/ folder")
            throw RuntimeException("fatalError failed to exit the process")
        }

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
        val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)

        var dirty = false
        val filtered = matrixMap.map.values.filter {
            val finished = it.state == MatrixState.FINISHED
            val notDownloaded = !it.downloaded
            finished && notDownloaded
        }

        // TODO: fetch in parallel https://github.com/TestArmada/flank/issues/323
        print(indent)
        filtered.forEach { matrix ->
            val prefix = Storage.BlobListOption.prefix(matrix.gcsPathWithoutRootBucket)
            val result = GcStorage.storage.list(matrix.gcsRootBucket, prefix, fields)

            result.iterateAll().forEach { blob ->
                val blobPath = blob.blobId.name
                if (
                    blobPath.matches(ArtifactRegex.testResultRgx)
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

    // used to update and poll the results from an async run
    suspend fun refreshLastRun() {
        val matrixMap = lastMatrices()
        val config = lastConfig()

        refreshMatrices(matrixMap, config)
        pollMatrices(matrixMap, config)
        fetchArtifacts(matrixMap)

        // Must generate reports *after* fetching xml artifacts since reports require xml
        val testsSuccessful = ReportManager.generate(matrixMap, config)
        if (!testsSuccessful) System.exit(1)
    }

    // used to cancel and update results from an async run
    fun cancelLastRun() {
        val matrixMap = lastMatrices()
        val config = lastConfig()

        cancelMatrices(matrixMap, config)
    }

    suspend fun newRun(config: IArgs) {
        println(config)
        val matrixMap = runTests(config)

        if (!config.async) {
            pollMatrices(matrixMap, config)
            fetchArtifacts(matrixMap)

            val testsSuccessful = ReportManager.generate(matrixMap, config)
            if (!testsSuccessful) System.exit(1)
        }
    }
}
