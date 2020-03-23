package ftl.run.model

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.Environment
import com.google.api.services.testing.model.IosDevice
import com.google.api.services.testing.model.TestDetails
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class RunningDevicesTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should poll matrices only for running android devices -- matrix already finished`() {
        mockkConstructor(RunningDevice::class)
        val (_, testMatrix, runningDevices) = makeCommonTestTriple(testState = "FINISHED")
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }

        verify(exactly = 1) { anyConstructed<RunningDevice>().poll(any()) }
        assertTrue(runningDevices.allComplete())
    }

    @Test
    fun `should poll matrices only for running android devices -- finished with success`() {
        mockkConstructor(RunningDevice::class)
        val (testExecution, testMatrix, runningDevices) = makeCommonTestTriple()

        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.state = "FINISHED"
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }

        verify(exactly = 3) { anyConstructed<RunningDevice>().poll(any()) }
        assertTrue(runningDevices.allComplete())
    }

    @Test
    fun `should poll matrices only for running android devices -- flaky with success`() {
        mockkConstructor(RunningDevice::class)
        val (testExecution, testMatrix, runningDevices) = makeCommonTestTriple()

        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.testDetails.progressMessages.addAll(listOf("."))
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.testDetails.progressMessages.addAll(listOf("."))
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.testDetails.progressMessages = listOf(".")
        testExecution.state = "FINISHED"
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }

        verify(exactly = 4) { anyConstructed<RunningDevice>().poll(any()) }
        assertTrue(runningDevices.allComplete())
    }

    @Test
    fun `should poll matrices only for running ios devices -- matrix already finished`() {
        mockkConstructor(RunningDevice::class)
        val (_, testMatrix, runningDevices) = makeCommonTestTriple(testState = "FINISHED", android = false)
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }

        verify(exactly = 1) { anyConstructed<RunningDevice>().poll(any()) }
        assertTrue(runningDevices.allComplete())
    }

    @Test
    fun `should poll matrices only for running ios devices -- finished with success`() {
        mockkConstructor(RunningDevice::class)
        val (testExecution, testMatrix, runningDevices) = makeCommonTestTriple(android = false)

        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.state = "FINISHED"
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }

        verify(exactly = 3) { anyConstructed<RunningDevice>().poll(any()) }
        assertTrue(runningDevices.allComplete())
    }

    @Test
    fun `should poll matrices only for running ios devices -- flaky with success`() {
        mockkConstructor(RunningDevice::class)
        val (testExecution, testMatrix, runningDevices) = makeCommonTestTriple()

        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.testDetails.progressMessages.addAll(listOf("."))
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.testDetails.progressMessages.addAll(listOf("."))
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        testExecution.testDetails.progressMessages = listOf(".")
        testExecution.state = "FINISHED"
        runningDevices.allRunning().forEach { it.poll(testMatrix) }
        runningDevices.allRunning().forEach { it.poll(testMatrix) }

        verify(exactly = 4) { anyConstructed<RunningDevice>().poll(any()) }
        assertTrue(runningDevices.allComplete())
    }

    @Test
    fun `allComplete should return false if tests matrix is still running`() {
        val (_, _, runningDevices) = makeCommonTestTriple()

        assertFalse(runningDevices.allComplete())
    }

    private fun makeCommonTestTriple(
        android: Boolean = true,
        modelId: String = "anyModelId",
        versionId: String = "anyStringId",
        testMatrixId: String = "anyMatrixId",
        testId: String = UUID.randomUUID().toString(),
        testState: String = "RUNNING",
        testErrorMessage: String = "anyErrorMessage",
        testProgressMessages: List<String> = mutableListOf(".")
    ): Triple<TestExecution, TestMatrix, RunningDevices> {
        val testExecution = makeTestExecution(
            android,
            modelId,
            versionId,
            testMatrixId,
            testId,
            testState,
            testErrorMessage,
            testProgressMessages
        )
        val testMatrix = TestMatrix().apply { testExecutions = listOf(testExecution) }
        val runningDevices = RunningDevices(mockk(relaxed = true), listOf(testExecution))
        return Triple(testExecution, testMatrix, runningDevices)
    }

    private fun makeTestExecution(
        android: Boolean = true,
        modelId: String = "anyModelId",
        versionId: String = "anyStringId",
        testMatrixId: String = "anyMatrixId",
        testId: String = UUID.randomUUID().toString(),
        testState: String = "RUNNING",
        testErrorMessage: String = "anyErrorMessage",
        testProgressMessages: List<String> = mutableListOf(".")
    ): TestExecution {
        val testEnvironment = Environment().apply {
            if (android) {
                androidDevice = AndroidDevice().apply {
                    androidModelId = modelId
                    androidVersionId = versionId
                }
            } else {
                iosDevice = IosDevice().apply {
                    iosModelId = modelId
                    iosVersionId = versionId
                }
            }
        }

        val details = TestDetails().apply {
            errorMessage = testErrorMessage
            progressMessages = testProgressMessages
        }

        return TestExecution().apply {
            matrixId = testMatrixId
            id = testId
            environment = testEnvironment
            state = testState
            testDetails = details
        }
    }
}
