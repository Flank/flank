import GcStorage.uploadApk
import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import com.google.testing.model.ToolResultsStep

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.TimeUnit

import Utils.sleep
import java.lang.System.currentTimeMillis
import java.nio.file.Path

object TestRunner {

    private val POLLING_TIMEOUT = (60 * 60 * 1000).toLong() // 60m
    private val POLLING_INTERVAL = 10 * 1000 // 10s
    private val FINISHED = "FINISHED"
    private val SUCCESS = "success"

    fun pollTests(testMatrixIds: ArrayList<String>): Boolean {
        val stopTime = currentTimeMillis() + POLLING_TIMEOUT
        val waitingOnIds = ArrayList<String>(testMatrixIds)
        val matrixCount = testMatrixIds.size
        val finishedTestMatrix = ArrayList<TestMatrix>(matrixCount)

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

            currentStatus = finishedTestMatrix.size.toString() + " / " + matrixCount + " complete"
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
                finishedTestMatrix.add(testMatrix)
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
        currentStatus = finishedTestMatrix.size.toString() + " / " + matrixCount + " complete."
        println(currentStatus)
        println()

        println("Fetching test results...")
        val results = fetchTestResults(finishedTestMatrix)

        var allTestsSuccessful = true
        var billableMinutes: Long = 0
        var successCount: Long = 0
        var failureCount: Long = 0
        val totalCount = testMatrixIds.size.toLong()
        for (result in results) {
            println(result)
            println()

            if (result.outcome == SUCCESS) {
                successCount += 1
            } else {
                allTestsSuccessful = false
                failureCount += 1
            }

            billableMinutes += result.billableMinutes
        }

        println(
                totalCount.toString() + " tests. successful: " + successCount + ", failed: " + failureCount)
        Billing.estimateCosts(billableMinutes)

        return allTestsSuccessful
    }

    private fun fetchTestResults(finishedTestMatrix: List<TestMatrix>): List<ToolResultsValue> {
        val steps = ArrayList<ToolResultsStep>()
        for (testMatrix in finishedTestMatrix) {
            for (testExecution in testMatrix.testExecutions) {
                steps.add(testExecution.toolResultsStep)
            }
        }

        val shardCount = steps.size
        val parallel = Parallel(shardCount)
        val toolValues = Collections.synchronizedList<ToolResultsValue>(ArrayList(shardCount))

        for (i in 0 until shardCount) {
            parallel.addCallable(ToolResultsCallable(toolValues, steps[i]))
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

        return ArrayList<String>(testMatrixIds)
    }
}
