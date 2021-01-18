package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.json.SavedMatrixTest.Companion.createResultsStorage
import ftl.json.SavedMatrixTest.Companion.createStepExecution
import ftl.json.SavedMatrixTest.Companion.testMatrix
import ftl.json.createSavedMatrix
import ftl.json.validate
import ftl.reports.outcome.TestOutcome
import ftl.run.cancelMatrices
import ftl.run.exception.CONFIGURATION_FAIL
import ftl.run.exception.FTLError
import ftl.run.exception.FailedMatrixError
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.FlankTimeoutError
import ftl.run.exception.GENERAL_FAILURE
import ftl.run.exception.MatrixCanceledError
import ftl.run.exception.NOT_PASSED
import ftl.run.exception.SUCCESS
import ftl.run.exception.UNEXPECTED_ERROR
import ftl.run.exception.YmlValidationError
import ftl.run.exception.withGlobalExceptionHandling
import ftl.test.util.FlankTestRunner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val VERIFICATION_FILE = "./should_exists.txt"
private const val VERIFICATION_MESSAGE = "Killing thread intentionally"

@RunWith(FlankTestRunner::class)
class UtilsTest {

    @get:Rule
    val exit = ExpectedSystemExit.none()!!

    @get:Rule
    val output = SystemOutRule().enableLog().muteForSuccessfulTests()!!

    @get:Rule
    val err = SystemErrRule().enableLog().muteForSuccessfulTests()!!

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = FlankGeneralError::class)
    fun `readTextResource errors`() {
        readTextResource("does not exist")
    }

    @Test
    fun `readTextResource succeeds`() {
        assertThat(readTextResource("version.txt")).isNotNull()
    }

    @Test
    fun `uniqueObjectName verifyPattern`() {
        val randomName = uniqueObjectName()
        assertThat(randomName.length).isEqualTo(31)
        assertThat(randomName[4]).isEqualTo('-')
        assertThat(randomName[7]).isEqualTo('-')
        assertThat(randomName[10]).isEqualTo('_')
        assertThat(randomName[13]).isEqualTo('-')
        assertThat(randomName[16]).isEqualTo('-')
        assertThat(randomName[19]).isEqualTo('.')
        assertThat(randomName[26]).isEqualTo('_')
        assertThat(randomName.last()).isNotEqualTo('/')
    }

    @Test(expected = FailedMatrixError::class)
    fun testExitCodeForFailed() {
        val testExecutions = listOf(
            createStepExecution(1, "Success"),
            createStepExecution(-1, "Failed")
        )
        val testMatrix = testMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage().apply {
            toolResultsExecution.executionId = "-1"
        }
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = createSavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("finishedMatrix" to finishedMatrix), "MockPath").validate()
    }

    @Test
    fun testExitCodeForSuccess() {
        val testExecutions = listOf(
            createStepExecution(1, "Success")
        )
        val testMatrix = testMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = createSavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath").validate()
    }

    @Test(expected = MatrixCanceledError::class)
    fun testExitCodeForInconclusive() { // inconclusive is treated as a failure
        val testExecutions = listOf(
            createStepExecution(-2, "Inconclusive")
        )
        val testMatrix = testMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage().apply {
            toolResultsExecution.executionId = "-2"
        }
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = createSavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath").validate()
    }

    @Test(expected = FTLError::class)
    fun testExitCodeForError() {
        val testExecutions = listOf(
            createStepExecution(-2, "Inconclusive"),
            createStepExecution(-3, "Skipped")
        )
        val testMatrix = testMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.ERROR
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val errorMatrix = createSavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("errorMatrix" to errorMatrix), "MockPath").validate()
    }

    @Test
    fun `should throw FailedMatrix with ignore set to true`() {
        val shouldIgnore = true
        val testExecutions = listOf(
            createStepExecution(1, "Success"),
            createStepExecution(-1, "Failed")
        )
        val testMatrix = testMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = createSavedMatrix(testMatrix)
        try {
            MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath").validate(shouldIgnore)
        } catch (t: FailedMatrixError) {
            assertTrue(t.ignoreFailed)
        } catch (_: Throwable) {
            fail()
        }
    }

    @Test
    fun `should terminate process with exit code 10 if FailedMatrix exception is thrown`() {
        // given
        val exception = FailedMatrixError(listOf(testMatrix1, testMatrix2))

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            {
                assertThat(it).isEqualTo(NOT_PASSED)
            }
        )
    }

    @Test
    fun `should terminate process with exit code 2 if YmlValidationError is thrown`() {
        // given
        val exception = YmlValidationError()

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            { exitCode ->
                assertThat(exitCode).isEqualTo(CONFIGURATION_FAIL)
            }
        )
    }

    @Test
    fun `should terminate process with exit code 1 and cancel running matrices if FlankTimeoutError is thrown`() {
        // given
        mockkStatic("ftl.run.CancelLastRunKt")
        coEvery { cancelMatrices(any(), any()) } just runs
        val exception = FlankTimeoutError(mapOf("anyMatrix" to mockk(relaxed = true)), "anyProject")

        // when-then
        withGlobalExceptionHandling(
            { throw exception },
            {
                coVerify(exactly = 1) { cancelMatrices(any(), any()) }
            }
        )
    }

    @Test
    fun `should terminate process with exit code 15 if FTLError is thrown`() {
        // given
        val exception = FTLError(testMatrix1)

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            { exitCode ->
                assertTrue(output.log.contains("Matrix is ${testMatrix1.state}"))
                assertThat(exitCode).isEqualTo(UNEXPECTED_ERROR)
            }
        )
    }

    @Test
    fun `should terminate process with exit code 2 if FlankFatalError is thrown`() {
        // given
        val message = "test error was thrown"
        val exception = FlankConfigurationError(message)

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            { exitCode ->
                assertTrue(err.log.contains(message))
                assertThat(exitCode).isEqualTo(CONFIGURATION_FAIL)
            }
        )
    }

    @Test
    fun `should terminate process with exit code 1 if not flank related exception is thrown`() {
        // given
        val message = "not flank related error thrown"

        val block = { throw FlankGeneralError(message) }

        // when
        withGlobalExceptionHandling(block) {
            assertThat(it).isEqualTo(GENERAL_FAILURE)
            assertTrue(err.log.contains(message))
        }
    }

    @Test
    fun `should notify sentry if non related flank error occurred`() {
        // given
        val message = "not flank related error thrown"
        val exception = Exception(message)
        mockkStatic(Throwable::report)

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            { exitCode ->
                assertThat(exitCode).isEqualTo(UNEXPECTED_ERROR)
                verify { exception.report() }
            }
        )
    }

    @Test
    fun `should terminate process with exit code 0 if at least one matrix failed and ignore-failed-tests flag is true`() {
        // given
        val exception = FailedMatrixError(
            matrices = listOf(testMatrix1, testMatrix2),
            ignoreFailed = true
        )

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            { exitCode ->
                assertThat(exitCode).isEqualTo(SUCCESS)
            }
        )
    }

    @Test
    fun `should terminate process with exit code 1 if there is not tests to run overall`() {
        // given
        val message = "No tests to run"
        val exception = FlankGeneralError(message)

        // when - then
        withGlobalExceptionHandling(
            { throw exception },
            { exitCode ->
                assertTrue(err.log.contains(message))
                assertThat(exitCode).isEqualTo(GENERAL_FAILURE)
            }
        )
    }

    @CommandLine.Command(name = "whosbad")
    private class Malicious : Runnable {
        override fun run() {
            val isThreadRunning = CountDownLatch(1)
            val forever: Thread = object : Thread("forever") {
                override fun run() {
                    try {
                        isThreadRunning.countDown()
                        sleep(Long.MAX_VALUE)
                    } catch (ignored: InterruptedException) {
                    }
                }
            }
            forever.isDaemon = false
            forever.start()
            try {
                isThreadRunning.await()
            } catch (ignored: InterruptedException) {
            }
            throw Error(VERIFICATION_MESSAGE)
        }
    }

    internal object HangingApp {
        @JvmStatic
        fun main(args: Array<String>) {
            FtlConstants.useMock = true
            withGlobalExceptionHandling { CommandLine(Malicious()).execute(*args) }
        }
    }

    @Test
    fun `should terminate if non-daemon thread launched from main thread throws an error`() {
        val processStarted = CountDownLatch(1)
        val completed = AtomicBoolean(false)
        val exitCode = AtomicInteger(Int.MIN_VALUE)
        val simulatedMain: Thread = object : Thread("simulated-main") {
            override fun run() {
                val pb = ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    HangingApp::class.java.name
                ).redirectError(File(VERIFICATION_FILE))
                try {
                    val process = pb.start()
                    processStarted.countDown()
                    exitCode.set(process.waitFor())
                    completed.set(true)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        simulatedMain.start()
        processStarted.await()
        simulatedMain.join(10 * 1000L)
        assertTrue("Our simulated main thread should have completed but instead it hung...", completed.get())
        assertEquals(UNEXPECTED_ERROR, exitCode.get())
        File(VERIFICATION_FILE).also {
            assertTrue("Verification file should exists, process might not have started", it.exists())
            assertTrue(it.inputStream().readBytes().toString(Charsets.UTF_8).contains(VERIFICATION_MESSAGE))
        }.delete()
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun removeVerificationFile() {
            File(VERIFICATION_FILE).run {
                if (exists()) delete()
            }
        }
    }
}

private val testMatrix1 = mockk<SavedMatrix>(relaxed = true) {
    every { matrixId } returns "1"
    every { webLink } returns "www.flank.com/1"
    every { testAxises } returns listOf(
        TestOutcome(
            outcome = "Failed",
            details = "Test failed to run"
        )
    )
}
private val testMatrix2 = mockk<SavedMatrix>(relaxed = true) {
    every { matrixId } returns "2"
    every { webLink } returns "www.flank.com/2"
    every { testAxises } returns listOf(
        TestOutcome(
            outcome = "Failed",
            details = "Test failed to run"
        )
    )
}
