package ftl.util

import com.google.api.services.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.json.SavedMatrixTest.Companion.createResultsStorage
import ftl.json.SavedMatrixTest.Companion.createStepExecution
import ftl.test.util.FlankTestRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import picocli.CommandLine
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(FlankTestRunner::class)
class UtilsTest {

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

    @Test
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
        val matrixMap = MatrixMap(mutableMapOf("finishedMatrix" to finishedMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(1)
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
        val matrixMap = MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(0)
    }

    @Test
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
        val matrixMap = MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(1)
    }

    @Test
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
        val matrixMap = MatrixMap(mutableMapOf("errorMatrix" to errorMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(2)
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
            throw Error("Killing the calling thread...")
        }
    }

    internal object HangingApp {
        @JvmStatic
        fun main(args: Array<String>) {
            jvmHangingSafe { CommandLine(Malicious()).execute(*args) }
        }
    }

    @Test
    @Throws(Exception::class)
    fun `should terminate if non-daemon thread launched from main thread throws an error`() {
        val processStarted = CountDownLatch(1)
        val completed = AtomicBoolean(false)
        val exitCode = AtomicInteger(Int.MIN_VALUE)
        val simulatedMain: Thread = object : Thread("simulated-main") {
            override fun run() {
                val pb = ProcessBuilder(
                    "java", "-cp", "classpath...picocli-4.2.0.jar", "ftl.util.UtilsTest\$HangingApp"
                )
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
        simulatedMain.join(3 * 1000L)
        Assert.assertTrue("Our simulated main thread should have completed but instead it hung...", completed.get())
        Assert.assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode.get())
    }
}
