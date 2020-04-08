package ftl.util

import com.google.api.services.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.json.SavedMatrixTest.Companion.createResultsStorage
import ftl.json.SavedMatrixTest.Companion.createStepExecution
import ftl.run.cancelMatrices
import ftl.test.util.FlankTestRunner
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
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

    @Test(expected = RuntimeException::class)
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
        assertThat(randomName.length).isEqualTo(32)
        assertThat(randomName[4]).isEqualTo('-')
        assertThat(randomName[7]).isEqualTo('-')
        assertThat(randomName[10]).isEqualTo('_')
        assertThat(randomName[13]).isEqualTo('-')
        assertThat(randomName[16]).isEqualTo('-')
        assertThat(randomName[19]).isEqualTo('.')
        assertThat(randomName[26]).isEqualTo('_')
    }

    @Test(expected = FailedMatrix::class)
    fun testExitCodeForFailed() {
        val testExecutions = listOf(
            createStepExecution(1, "Success"),
            createStepExecution(-1, "Failed")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("finishedMatrix" to finishedMatrix), "MockPath").validateMatrices()
    }

    @Test
    fun testExitCodeForSuccess() {
        val testExecutions = listOf(
            createStepExecution(1, "Success")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath").validateMatrices()
    }

    @Test(expected = FailedMatrix::class)
    fun testExitCodeForInconclusive() { // inconclusive is treated as a failure
        val testExecutions = listOf(
            createStepExecution(-2, "Inconclusive")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath").validateMatrices()
    }

    @Test(expected = FTLError::class)
    fun testExitCodeForError() {
        val testExecutions = listOf(
            createStepExecution(-2, "Inconclusive"),
            createStepExecution(-3, "Skipped")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.ERROR
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val errorMatrix = SavedMatrix(testMatrix)
        MatrixMap(mutableMapOf("errorMatrix" to errorMatrix), "MockPath").validateMatrices()
    }

    @Test
    fun `should throw FailedMatrix with ignore set to true`() {
        val shouldIgnore = true
        val testExecutions = listOf(
            createStepExecution(1, "Success"),
            createStepExecution(-1, "Failed")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        try {
            MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath").validateMatrices(shouldIgnore)
        } catch (t: FailedMatrix) {
            assertTrue(t.ignoreFailed)
        } catch (_: Throwable) {
            fail()
        }
    }

    @Test
    fun `should terminate process with exit code 1 if FailedMatrix exception is thrown`() {
        // given
        exit.expectSystemExitWithStatus(1)
        val block = {
            throw FailedMatrix(
                listOf(
                    mockk(relaxed = true) { every { matrixId } returns "1" },
                    mockk(relaxed = true) { every { matrixId } returns "2" }
                )
            )
        }
        // when
        withGlobalExceptionHandling(block)
        // then
        assertTrue(output.log.contains("Error: Matrix failed: 1"))
        assertTrue(output.log.contains("Error: Matrix failed: 2"))
    }

    @Test
    fun `should terminate process with exit code 1 if YmlValidationError is thrown`() {
        exit.expectSystemExitWithStatus(1)
        val block = { throw YmlValidationError() }
        withGlobalExceptionHandling(block)
    }

    @Test
    fun `should terminate process with exit code 1 and cancel running matrices if FlankTimeoutError is thrown`() {
        // given
        mockkStatic("ftl.run.CancelLastRunKt")
        coEvery { cancelMatrices(any(), any()) } just runs
        exit.expectSystemExitWithStatus(1)
        val block = { throw FlankTimeoutError(mapOf("anyMatrix" to mockk(relaxed = true)), "anyProject") }
        // when
        withGlobalExceptionHandling(block)
        // then
        coVerify(exactly = 1) { cancelMatrices(any(), any()) }
    }

    @Test
    fun `should terminate process with exit code 3 if FTLError is thrown`() {
        // given
        exit.expectSystemExitWithStatus(3)
        val block = { throw FTLError(mockk(relaxed = true)) }
        // when
        withGlobalExceptionHandling(block)
        // then
        assertTrue(output.log.contains("Error: Matrix not finished:"))
    }

    @Test
    fun `should terminate process with exit code 2 if FlankFatalError is thrown`() {
        // given
        val message = "test error was thrown"
        exit.expectSystemExitWithStatus(2)
        val block = { throw FlankFatalError(message) }
        // when
        withGlobalExceptionHandling(block)
        // then
        assertTrue(err.log.contains(message))
    }

    @Test
    fun `should terminate process with exit code 3 if not flank related exception is thrown`() {
        // given
        val message = "not flank related error thrown"
        val spy = spyk<FtlConstants>()
        exit.expectSystemExitWithStatus(3)
        val block = { throw RuntimeException(message) }
        // when
        withGlobalExceptionHandling(block)
        // then
        assertTrue(err.log.contains(message))

        // this is extra check to verify if test errors are not reported to Bugsnag
        // should be removed or changed when https://github.com/Flank/flank/issues/699 is resolved
        verify { spy.bugsnag?.wasNot(called) }
    }

    @Test
    fun `should notify bugsnag if non related flak error occurred`() {
        // given
        val message = "not flank related error thrown"
        mockkObject(FtlConstants)
        every { FtlConstants.useMock } returns false
        every { FtlConstants.bugsnag } returns mockk() {
            every { notify(any<Throwable>()) } returns true
        }
        exit.expectSystemExitWithStatus(3)
        val block = { throw RuntimeException(message) }
        // when
        withGlobalExceptionHandling(block)
        // then
        assertTrue(err.log.contains(message))

        verify(exactly = 1) { FtlConstants.useMock }
        verify(exactly = 1) { FtlConstants.bugsnag?.notify(any<Throwable>()) }
    }

    @Test
    fun `should terminate process with exit code 0 if at least one matrix failed and ignore-failed-tests flag is true`() {
        // given
        exit.expectSystemExitWithStatus(0)
        val block = {
            throw FailedMatrix(
                matrices = listOf(
                    mockk(relaxed = true) { every { matrixId } returns "1" },
                    mockk(relaxed = true) { every { matrixId } returns "2" }
                ),
                ignoreFailed = true
            )
        }
        // when
        withGlobalExceptionHandling(block)
        // then
        assertTrue(output.log.contains("Error: Matrix failed: 1"))
        assertTrue(output.log.contains("Error: Matrix failed: 2"))
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
                    "java", "-cp", System.getProperty("java.class.path"), HangingApp::class.java.name
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
        assertEquals(3, exitCode.get())
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
