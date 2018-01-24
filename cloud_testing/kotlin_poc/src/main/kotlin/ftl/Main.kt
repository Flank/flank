package ftl

import com.google.cloud.storage.Storage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.testing.Testing
import com.google.testing.model.TestMatrix
import com.linkedin.dex.parser.DexParser
import ftl.config.FtlConstants
import ftl.config.FtlConstants.appApk
import ftl.config.FtlConstants.localResultsDir
import ftl.config.FtlConstants.matrixIdsFile
import ftl.config.FtlConstants.testApk
import ftl.config.RunConfig
import ftl.gc.GcAndroidMatrix
import ftl.gc.GcStorage.storage
import ftl.gc.GcStorage.uploadApk
import ftl.gc.GcTestMatrix
import ftl.gc.GcTesting
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.util.ArtifactRegex.screenshotRgx
import ftl.util.ArtifactRegex.testResultRgx
import ftl.util.MatrixState
import ftl.util.MatrixState.FINISHED
import ftl.util.Outcome
import ftl.util.StopWatch
import ftl.util.Utils.sleep
import ftl.util.Utils.uniqueObjectName
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.stream.Collectors

object Main {
    private var useMock = false
    private val gson = GsonBuilder().setPrettyPrinting().create()!!

    init {
        if (useMock) {
            // Use mock server
            Testing.DEFAULT_ROOT_URL = "http://localhost:4010/"
        }
    }

    private fun assertMockUrl() {
        if (!useMock) return
        val create = GcTesting.get.projects().testMatrices().create(FtlConstants.projectId, TestMatrix())
        val baseUrl = create.abstractGoogleClient.baseUrl
        if (!baseUrl.contains("localhost")) throw RuntimeException("expected localhost")
    }

    private suspend fun runTests(shardCount: Int, testMethods: List<String>?): MatrixMap {
        println("runTests")
        val stopwatch = StopWatch().start()
        assertMockUrl()

        val runGcsPath = uniqueObjectName()
        val runConfig = RunConfig(testTimeoutMinutes = 60)
        var testTargets: List<String>? = null

        if (testMethods != null) {
            val apkTestMethods = DexParser.findTestNames(testApk.toString())

            val missingMethods = mutableListOf<String>()
            testMethods.forEach { testMethod ->
                if (!apkTestMethods.contains(testMethod)) {
                    missingMethods.add(testMethod)
                }
            }

            if (missingMethods.isNotEmpty()) {
                throw RuntimeException("Methods not found in test apk: $missingMethods")
            }

            testTargets = testMethods.stream().map { i -> "class " + i }.collect(Collectors.toList())
        }

        // GcAndroidMatrix => GcTestMatrix
        // GcTestMatrix.execute() 3x retry => matrix id (string)
        val androidMatrix = GcAndroidMatrix.build(
                "NexusLowRes",
                "26",
                "en",
                "portrait")

        val apks = uploadApksInParallel(appApk, testApk, runGcsPath)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        repeat(shardCount) {
            jobs += async {
                GcTestMatrix.build(
                        apks.first,
                        apks.second,
                        runGcsPath,
                        androidMatrix,
                        testTargets,
                        runConfig = runConfig).execute()
            }
        }

        val savedMatrices = mutableMapOf<String, SavedMatrix>()

        jobs.forEach {
            val matrix = it.await()
            val matrixId = matrix.testMatrixId
            savedMatrices[matrixId] = SavedMatrix(matrix)
        }

        val matrixMap = MatrixMap(savedMatrices, runGcsPath)

        updateMatrixFile(matrixMap)

        println("  ${savedMatrices.size} matrix ids created in ${stopwatch.end()}")

        return matrixMap
    }

    private fun updateMatrixFile(matrixMap: MatrixMap) {
        val matrixIdsFile = Paths.get(FtlConstants.localResultsDir, matrixMap.runPath, matrixIdsFile)
        matrixIdsFile.parent.toFile().mkdirs()
        Files.write(matrixIdsFile, gson.toJson(matrixMap.map).toByteArray())
    }

    /** @return Pair(app apk, test apk) **/
    private suspend fun uploadApksInParallel(appApk: Path, testApk: Path, runGcsPath: String): Pair<String, String> {
        if (useMock) return Pair("", "")

        val appApkGcsPath = async { uploadApk(appApk, runGcsPath) }
        val testApkGcsPath = async { uploadApk(testApk, runGcsPath) }

        return Pair(appApkGcsPath.await(), testApkGcsPath.await())
    }

    private fun deleteResults() {
        val resultsFile = Paths.get(FtlConstants.localResultsDir).toFile()
        resultsFile.deleteRecursively()
        resultsFile.mkdirs()
    }

    /** Refresh all in progress matrices in parallel **/
    suspend fun refreshMatrices(matrixMap: MatrixMap) {
        println("refreshMatrices")

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val map = matrixMap.map
        var matrixCount = 0
        map.forEach { matrix ->
            // Only refresh unfinished
            if (MatrixState.inProgress(matrix.value.state)) {
                matrixCount += 1
                jobs += async { GcTestMatrix.refresh(matrix.key) }
            }
        }

        if (matrixCount != 0) {
            println("  Refreshing ${matrixCount}x matrices")
        }

        var dirty = false
        jobs.forEach {
            val matrix = it.await()
            val matrixId = matrix.testMatrixId

            println("${matrix.state} $matrixId")

            if (map[matrixId]?.update(matrix) == true) dirty = true
        }

        if (dirty) {
            println("  Updating matrix file")
            updateMatrixFile(matrixMap)
        }
    }

    fun lastGcsPath(): String {
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
    fun lastMatrices(): MatrixMap {
        val lastRun = lastGcsPath()
        println("Loading run $lastRun")

        val json = Paths.get(FtlConstants.localResultsDir, lastRun, matrixIdsFile).toFile().readText()

        val listOfSavedMatrix = object : TypeToken<MutableMap<String, SavedMatrix>>() {}.type
        val map: MutableMap<String, SavedMatrix> = gson.fromJson(json, listOfSavedMatrix)

        return MatrixMap(map, lastRun)
    }

    /** fetch test_result_0.xml & *.png **/
    fun fetchArtifacts(matrixMap: MatrixMap) {
        println("fetchArtifacts")
        val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)

        var dirty = false
        val filtered = matrixMap.map.values.filter {
            val finished = it.state == FINISHED
            val notDownloaded = !it.downloaded
            val failure = it.outcome == Outcome.failure
            finished && notDownloaded && failure
        }

        filtered.forEach { matrix ->
            val prefix = Storage.BlobListOption.prefix(matrix.gcsPathWithoutRootBucket)
            val result = storage.list(matrix.gcsRootBucket, prefix, fields)

            result.iterateAll().forEach({ blob ->
                val blobPath = blob.blobId.name
                if (
                        blobPath.matches(testResultRgx) ||
                        blobPath.matches(screenshotRgx)
                ) {
                    val downloadFile = Paths.get(localResultsDir, blobPath)
                    if (downloadFile.toFile().exists()) {
                        println("  Already downloaded: $blobPath")
                    } else {
                        println("  Downloading: $blobPath")
                        downloadFile.parent.toFile().mkdirs()
                        blob.downloadTo(downloadFile)
                    }
                }
            })

            dirty = true
            matrix.downloaded = true
        }

        if (dirty) {
            println("  Updating matrix file")
            updateMatrixFile(matrixMap)
        }
    } // fun

    /** Synchronously poll all matrix ids until they complete **/
    fun pollMatrices(matrices: MatrixMap) {
        println("pollMatrices")
        val poll = matrices.map.values.filter {
            MatrixState.inProgress(it.state)
        }

        val stopwatch = StopWatch()
        poll.forEach {
            stopwatch.reset().start()
            val matrixId = it.matrixId

            while (true) {
                val refreshedMatrix = GcTestMatrix.refresh(matrixId)
                it.update(refreshedMatrix)

                val state = refreshedMatrix.state
                println("  $matrixId $state ${stopwatch.end()}")

                if (!MatrixState.inProgress(state)) break
                sleep(15)
            }
        }

        updateMatrixFile(matrices)
    }

    // used to update results from an async run
    suspend fun refreshLastRun() {
        val matrixMap = lastMatrices()

        refreshMatrices(matrixMap)
        fetchArtifacts(matrixMap)
    }

    suspend fun newRun(shardCount: Int, waitForResults: Boolean = true, testMethods: List<String>?) {
        val matrixMap = runTests(shardCount, testMethods)

        if (waitForResults) {
            pollMatrices(matrixMap)
            fetchArtifacts(matrixMap)
        }
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            // deleteResults()
            newRun(shardCount = 1,
                    waitForResults = true,
                    testMethods = listOf("com.example.app.ExampleUiTest#testPasses"))
            // refreshLastRun()
        }
    }
}
