package ftl

import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import ftl.GcStorage.uploadApk
import ftl.Utils.sleep
import java.lang.System.currentTimeMillis
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

object TestRunner {

    private const val POLLING_TIMEOUT = (60 * 60 * 1000).toLong() // 60m
    private const val POLLING_INTERVAL = 10 * 1000 // 10s
    private const val FINISHED = "FINISHED"
    private const val SUCCESS = "success"
    private val storage = StorageOptions.newBuilder().build().service
    private val testResultRgx = Regex(".*test_result_\\d+.xml$")

    // gcsFolder = "2018-01-17_19:38:56.695000_fCMj"
    private fun downloadXml(gcsFolder: String) {
        if (!GlobalConfig.downloadXml) return
        var xmlCount = -1

        val prefix = Storage.BlobListOption
                .prefix(gcsFolder)

        val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)
        val result = storage.list(GlobalConfig.bucketGcsPath, prefix, fields)

        result.iterateAll().forEach({
            val name = it.blobId.name
            if (name.matches(testResultRgx)) {
                xmlCount += 1
                println("Downloading: $name")
                it.downloadTo(Paths.get(gcsFolder, "test_result_$xmlCount.xml"))
            }
        })
    }

    fun pollTests(testMatrixIds: ArrayList<String>): Boolean {
        val stopTime = currentTimeMillis() + POLLING_TIMEOUT
        val waitingOnIds = ArrayList<String>(testMatrixIds)
        val matrixCount = testMatrixIds.size
        val finishedTestMatrixArray = ArrayList<TestMatrix>(matrixCount)

        var invalid = false
        var lastStatus = ""
        var currentStatus: String
        var lastState: String? = ""
        var lastTestExecution: TestExecution? = null

        while (waitingOnIds.size > 0) {
            if (currentTimeMillis() > stopTime) {
                throw RuntimeException(
                        "Polling timed out after "
                                + TimeUnit.MILLISECONDS.toMinutes(POLLING_TIMEOUT)
                                + " minutes")
            }

            currentStatus = finishedTestMatrixArray.size.toString() + " / " + matrixCount + " complete"
            if (currentStatus != lastStatus) {
                println(currentStatus)
            }
            lastStatus = currentStatus

            val testMatrixId = waitingOnIds[0]
            val testMatrix = GcTestMatrix.refresh(testMatrixId)

            var finished = true
            for (testExecution in testMatrix.testExecutions) {
                lastState = testExecution.state
                println(testExecution.state)

                if (!TestExecutionState.isValid(lastState)) {
                    // TEST_ONLY_APK: The APK is marked as "testOnly".
                    // Use Build > Build APK(s) in Android Studio to build the app apk.
                    // App APKs built from using 'Run' in Android Studio don't work on Firebase Test Lab
                    println("Error: " + testExecution.testDetails.errorMessage)
                    invalid = true
                    lastTestExecution = testExecution
                    break
                }

                if (FINISHED != lastState) {
                    finished = false
                    break
                }
            }

            if (invalid) {
                break
            }

            if (finished) {
                finishedTestMatrixArray.add(testMatrix)
                waitingOnIds.removeAt(0)
            } else {
                sleep(POLLING_INTERVAL)
            }
        }

        if (lastState == null) lastState = ""

        if (invalid) {
            // infrastructure errors are transparently retried up to 3 attempts.
            if (lastState == TestExecutionState.ERROR && lastTestExecution != null) {
                println(lastTestExecution.testDetails.errorMessage)
            }
            for (matrixId in waitingOnIds) {
                GcTestMatrix.cancel(matrixId)
            }

            // Invalid state detected: ERROR is an Infrastructure failure.
            throw RuntimeException("Invalid state detected: " + lastState)
        }

        println("Test run finished")
        currentStatus = finishedTestMatrixArray.size.toString() + " / " + matrixCount + " complete."
        println(currentStatus)
        println()

        println("Fetching test results...")
        val results = fetchTestResults(finishedTestMatrixArray)

        var allTestsSuccessful = true
        var billableMinutes: Long = 0
        var successCount: Long = 0
        var failureCount: Long = 0
        val totalCount = testMatrixIds.size.toLong()
        for (result in results) {
            println(result)
            println()

            // What gcs object is associated with this result?
            // "executionId" -> "6296919122126826652"
            // "historyId" -> "bh.58317d9cd7ab9ba2"
            // "projectId" -> "delta-essence-114723"
            // "stepId" -> "bs.e7bbe13221894d52"
            if (result.outcome == SUCCESS) {
                successCount += 1
            } else {
                allTestsSuccessful = false
                failureCount += 1

                // Only download XML on failed runs
                downloadXml(result.gcsPath)
            }

            billableMinutes += result.billableMinutes
        }

        println(
                totalCount.toString() + " tests. successful: " + successCount + ", failed: " + failureCount)
        Billing.estimateCosts(billableMinutes)

        return allTestsSuccessful
    }

    private fun fetchTestResults(finishedTestMatrix: List<TestMatrix>): List<ToolResultsValue> {
        val steps = ArrayList<ToolResultsStepGcsPath>()
        for (testMatrix in finishedTestMatrix) {
            val gcsPath = testMatrix.resultStorage.googleCloudStorage.gcsPath.split('/')[1]

            testMatrix.testExecutions
                    .map { it.toolResultsStep }
                    .mapTo(steps) { ToolResultsStepGcsPath(it, gcsPath) }
        }

        val shardCount = steps.size
        val parallel = Parallel(shardCount)
        val toolValues = Collections.synchronizedList<ToolResultsValue>(ArrayList(shardCount))

        for (step in steps) {
            parallel.addCallable(ToolResultsCallable(toolValues, step))
        }

        parallel.run()

        if (shardCount != toolValues.size || shardCount != toolValues.size) {
            throw RuntimeException("Synchronization error")
        }

        return toolValues
    }

    fun scheduleApks(appApk: Path, testApk: Path, shardCount: Int, runConfig: RunConfig): ArrayList<String> {
        val appApkGcsPath = uploadApk(appApk)
        val testApkGcsPath = uploadApk(testApk)

        // *MUST* use synchronized list to play nice with ExecutorService
        val testMatrixIds = Collections.synchronizedList<String>(ArrayList(shardCount))
        val parallel = Parallel(shardCount)

        println("Running ${shardCount}x")
        repeat(shardCount) {
            val androidMatrix = GcAndroidMatrix.build("NexusLowRes", "26", "en", "portrait")

            val testMatrixCreate = GcTestMatrix.build(appApkGcsPath, testApkGcsPath, androidMatrix, runConfig = runConfig)

            parallel.addCallable(MatrixCallable(testMatrixIds, testMatrixCreate))
        }

        parallel.run()

        if (shardCount != testMatrixIds.size || shardCount != testMatrixIds.size) {
            throw RuntimeException("Synchronization error")
        }

        return ArrayList(testMatrixIds)
    }

    /** Runs 1 test per VM **/
    fun scheduleTests(
            appApkGcsPath: String, testApkGcsPath: String, testMethodNames: List<String>, runConfig: RunConfig): ArrayList<String> {

        val shardCount = testMethodNames.size

        // *MUST* use synchronized list to play nice with ExecutorService
        val testMatrixIds = Collections.synchronizedList<String>(ArrayList(shardCount))
        val parallel = Parallel(shardCount)

        for (testMethod in testMethodNames) {
            val androidMatrix = GcAndroidMatrix.build("NexusLowRes", "25", "en", "portrait")

            val testMatrixCreate = GcTestMatrix.build(appApkGcsPath, testApkGcsPath, androidMatrix, testMethod, runConfig = runConfig)

            parallel.addCallable(MatrixCallable(testMatrixIds, testMatrixCreate))
        }

        parallel.run()

        if (shardCount != testMatrixIds.size || shardCount != testMatrixIds.size) {
            throw RuntimeException("Synchronization error")
        }

        return ArrayList(testMatrixIds)
    }
}
