package ftl.api

import com.google.common.truth.Truth.assertThat
import com.google.testing.model.Environment
import com.google.testing.model.FileReference
import com.google.testing.model.GoogleCloudStorage
import com.google.testing.model.ResultStorage
import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import com.google.testing.model.TestSpecification
import com.google.testing.model.ToolResultsExecution
import com.google.testing.model.ToolResultsStep
import ftl.adapter.google.toApiModel
import ftl.config.Device
import ftl.domain.testmatrix.updateWithMatrix
import ftl.gc.GcAndroidDevice
import ftl.json.createAndUpdateMatrix
import ftl.reports.outcome.make
import ftl.test.util.FlankTestRunner
import ftl.util.MatrixState.FINISHED
import ftl.util.MatrixState.INVALID
import ftl.util.MatrixState.PENDING
import ftl.util.webLink
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

// TODO: fix naming
@RunWith(FlankTestRunner::class)
class TestMatrixTest {

    companion object {
        const val projectId = "1"
        const val historyId = "2"
        const val executionId = "1"
        const val testMatrixId = "123"

        // use -1 step id to get a failure outcome from the mock server
        fun createStepExecution(stepId: Int, deviceModel: String = "shamu", executionId: Int? = null): TestExecution {
            val toolResultsStep = ToolResultsStep()
            toolResultsStep.projectId = projectId
            toolResultsStep.historyId = historyId
            toolResultsStep.executionId = executionId?.toString() ?: stepId.toString()
            toolResultsStep.stepId = stepId.toString()

            val testExecution = TestExecution()
            testExecution.toolResultsStep = toolResultsStep

            val androidDevice = GcAndroidDevice.build(Device(deviceModel, "23"))
            testExecution.environment = Environment().setAndroidDevice(androidDevice)

            return testExecution
        }

        private const val mockFileName = "mockFileName"
        private const val mockBucket = "mockBucket"
        private val mockGcsPath = "$mockBucket/$mockFileName"

        fun createResultsStorage() = ResultStorage().apply {
            googleCloudStorage = GoogleCloudStorage().apply {
                gcsPath = mockGcsPath
            }
            toolResultsExecution = ToolResultsExecution().also {
                it.historyId = historyId
                it.projectId = projectId
                it.executionId = executionId
            }
        }

        fun ftlTestMatrix(block: TestMatrix.() -> Unit = {}) = TestMatrix().also {
            it.projectId = projectId
            it.testMatrixId = testMatrixId
            it.block()
        }
    }

    @Test
    fun `testMatrix failureOutcome`() {
        // Verify that if we have two executions: failure then success
        // the SavedMatrix outcome is correctly recorded as failure
        val testExecutions = listOf(
            createStepExecution(-1),
            createStepExecution(1)
        )

        val matrixId = "123"
        val matrixState = FINISHED
        val testMatrix = ftlTestMatrix()
        testMatrix.testMatrixId = matrixId
        testMatrix.state = matrixState
        testMatrix.resultStorage = createResultsStorage().apply {
            toolResultsExecution.executionId = "-1"
        }
        testMatrix.testExecutions = testExecutions

        val testMatrixData = createAndUpdateMatrix(testMatrix)
        assertThat(testMatrixData.outcome).isEqualTo("failure")

        // assert other properties
        assertThat(testMatrixData.matrixId).isEqualTo(matrixId)
        assertThat(testMatrixData.state).isEqualTo(matrixState)
        assertThat(testMatrixData.gcsPath).isEqualTo(mockGcsPath)
        assertThat(testMatrixData.webLink).isEqualTo("https://console.firebase.google.com/project/1/testlab/histories/2/matrices/-1")
        assertThat(testMatrixData.downloaded).isFalse()
        assertThat(testMatrixData.billableMinutes.virtual).isEqualTo(1)
        assertThat(testMatrixData.billableMinutes.physical).isEqualTo(1)
        assertThat(testMatrixData.gcsPathWithoutRootBucket).isEqualTo(mockFileName)
        assertThat(testMatrixData.gcsRootBucket).isEqualTo(mockBucket)
        assertThat(testMatrixData.axes.first().details).isNotEmpty()
    }

    @Test
    fun `testMatrix skippedOutcome`() {
        // Verify that if we have two executions: skipped
        // the SavedMatrix outcome is correctly recorded as skipped
        val testExecutions = listOf(
            createStepExecution(-3)
        )

        val matrixId = "123"
        val matrixState = FINISHED
        val ftlTestMatrix = ftlTestMatrix()
        ftlTestMatrix.testMatrixId = matrixId
        ftlTestMatrix.state = matrixState
        ftlTestMatrix.resultStorage = createResultsStorage().apply {
            toolResultsExecution.executionId = "-3"
        }
        ftlTestMatrix.testExecutions = testExecutions

        val testMatrix = createAndUpdateMatrix(ftlTestMatrix)
        assertThat(testMatrix.outcome).isEqualTo("skipped")

        // assert other properties
        assertThat(testMatrix.matrixId).isEqualTo(matrixId)
        assertThat(testMatrix.state).isEqualTo(matrixState)
        assertThat(testMatrix.gcsPath).isEqualTo(mockGcsPath)
        assertThat(testMatrix.webLink).isEqualTo("https://console.firebase.google.com/project/1/testlab/histories/2/matrices/-3/executions/-3")
        assertThat(testMatrix.downloaded).isFalse()
        assertThat(testMatrix.billableMinutes.virtual).isEqualTo(1)
        assertThat(testMatrix.billableMinutes.physical).isEqualTo(1)
        assertThat(testMatrix.gcsPathWithoutRootBucket).isEqualTo(mockFileName)
        assertThat(testMatrix.gcsRootBucket).isEqualTo(mockBucket)
        assertThat(testMatrix.axes.first().details).isNotEmpty()
    }

    @Test
    fun `testMatrix update`() {
        val testExecutions = listOf(
            createStepExecution(1, "shamu"),
            createStepExecution(1, "NexusLowRes")
        )

        val matrixId = "123"
        val ftlTestMatrix = ftlTestMatrix()
        ftlTestMatrix.testMatrixId = matrixId
        ftlTestMatrix.state = PENDING
        ftlTestMatrix.resultStorage = createResultsStorage()
        ftlTestMatrix.testExecutions = testExecutions

        var testMatrix = ftlTestMatrix.toApiModel()

        assert(testMatrix.state != FINISHED)
        ftlTestMatrix.state = FINISHED
        ftlTestMatrix.webLink()
        testMatrix = testMatrix.updateWithMatrix(ftlTestMatrix.toApiModel())
        assert(testMatrix.state == FINISHED)
    }

    @Test
    fun `testMatrix on finish should calculate cost when state != ERROR`() {
        val testExecutions = listOf(
            createStepExecution(1, "shamu"),
            createStepExecution(1, "NexusLowRes")
        )
        val ftlTestMatrix = ftlTestMatrix()
        ftlTestMatrix.projectId = projectId
        ftlTestMatrix.testMatrixId = "123"
        ftlTestMatrix.state = PENDING
        ftlTestMatrix.resultStorage = createResultsStorage()
        ftlTestMatrix.testExecutions = testExecutions

        var testMatrix = ftlTestMatrix.toApiModel()

        ftlTestMatrix.state = FINISHED
        ftlTestMatrix.webLink()
        testMatrix = testMatrix.updateWithMatrix(ftlTestMatrix.toApiModel())
        assertEquals(1, testMatrix.billableMinutes.virtual)
        assertEquals(1, testMatrix.billableMinutes.physical)
    }

    @Test
    fun `testMatrix should have outcome and outcome details properly filled when state is INVALID`() {
        val expectedOutcome = "INVALID"
        val expectedOutcomeDetails = "UNKNOWN"
        val ftlTestMatrix = ftlTestMatrix()
        ftlTestMatrix.testMatrixId = "123"
        ftlTestMatrix.state = PENDING
        ftlTestMatrix.resultStorage = createResultsStorage()

        var testMatrix = ftlTestMatrix.toApiModel()

        ftlTestMatrix.state = INVALID
        testMatrix = testMatrix.updateWithMatrix(ftlTestMatrix.toApiModel())
        assertEquals(expectedOutcome, testMatrix.outcome)
        assertEquals(expectedOutcomeDetails, testMatrix.axes.first().details)
        assertEquals(INVALID, testMatrix.state)
    }

    @Test
    fun `testMatrix should have failed outcome when at least one test is failed`() {
        val expectedOutcome = "failure"
        val successStepExecution = createStepExecution(1) // success
        val failedStepExecution = createStepExecution(-1) // failure
        val flakyStepExecution = createStepExecution(-4) // flaky
        // https://github.com/Flank/flank/issues/914
        // This test covers edge case where the last test execution to check is flaky
        // based on different outcome from step (failed) and execution (success)
        // step.outcome != execution.outcome => means flaky
        val flakyOutcomeComparedStepExecution = createStepExecution(stepId = -1, executionId = 1) // flaky

        // below order in the list matters!
        val executions = listOf(
            flakyStepExecution,
            successStepExecution,
            failedStepExecution,
            flakyOutcomeComparedStepExecution
        )

        val ftlTestMatrix = ftlTestMatrix().apply {
            testMatrixId = "123"
            state = FINISHED
            resultStorage = createResultsStorage().apply {
                toolResultsExecution.executionId = "-1"
            }
            testExecutions = executions
        }

        val testMatrix = createAndUpdateMatrix(ftlTestMatrix)

        assertEquals(
            "Does not return failed outcome when last execution is flaky",
            expectedOutcome,
            testMatrix.outcome
        )
    }

    @Test
    fun `testMatrix should be updated with apk file name - android`() {
        val appName = "any-test_app.apk"

        val specs = listOf<TestSpecification.() -> Unit>(
            { androidInstrumentationTest = make { appApk = ref { "gs://any/path/to/app/$appName" } } },
            { androidTestLoop = make { appApk = ref { "gs://any/path/to/app/$appName" } } },
            { androidRoboTest = make { appApk = ref { "gs://any/path/to/app/$appName" } } },
        )

        val getNewTestMatrix = {
            ftlTestMatrix {
                state = PENDING
                resultStorage = createResultsStorage()
                testExecutions = listOf(createStepExecution(1, "NexusLowRes"))
            }
        }

        specs.forEach { spec ->
            val ftlTestMatrix = getNewTestMatrix()
            val testMatrix = ftlTestMatrix.toApiModel()

            ftlTestMatrix.state = FINISHED
            ftlTestMatrix.testSpecification = make(spec)
            val updatedMatrix = testMatrix.updateWithMatrix(ftlTestMatrix.toApiModel())
            assertEquals(appName, updatedMatrix.appFileName)
        }
    }

    @Test
    fun `SavedMatrix should be updated with apk file name - ios`() {
        val appName = "any-test_app.zip"

        val specs = listOf<TestSpecification.() -> Unit>(
            { iosXcTest = make { testsZip = ref { "gs://any/path/to/app/$appName" } } },
            { iosTestLoop = make { appIpa = ref { "gs://any/path/to/app/$appName" } } },
        )

        val getNewTestMatrix = {
            ftlTestMatrix {
                state = PENDING
                resultStorage = createResultsStorage()
                testExecutions = listOf(createStepExecution(1, "iPhone6"))
            }
        }

        specs.forEach { spec ->
            val ftlTestMatrix = getNewTestMatrix()
            val testMatrix = ftlTestMatrix.toApiModel()

            ftlTestMatrix.state = FINISHED
            ftlTestMatrix.testSpecification = make(spec)
            val updatedMatrix = testMatrix.updateWithMatrix(ftlTestMatrix.toApiModel())
            assertEquals(appName, updatedMatrix.appFileName)
        }
    }

    @Test
    fun `SavedMatrix should be updated with NA file name if none is available`() {
        val getNewTestMatrix = {
            ftlTestMatrix {
                state = PENDING
                resultStorage = createResultsStorage()
                testExecutions = listOf(createStepExecution(1, "iPhone6"))
            }
        }

        val ftlTestMatrix = getNewTestMatrix()
        val testMatrix = ftlTestMatrix.toApiModel()

        ftlTestMatrix.state = FINISHED
        val updatedMatrix = testMatrix.updateWithMatrix(ftlTestMatrix.toApiModel())
        assertEquals("N/A", updatedMatrix.appFileName)
    }
}

private inline fun ref(path: () -> String) = FileReference().apply { gcsPath = path() }
