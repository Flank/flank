package ftl

import com.google.testing.Testing.Projects.TestMatrices.Create
import com.google.testing.model.TestMatrix
import java.util.concurrent.Callable

import ftl.Utils.fatalError
import ftl.Utils.sleep

internal class MatrixCallable(private val testMatrixIds: MutableList<String>,
                              private val testMatrixCreate: Create) : Callable<Any> {

    /** Returns test matrix id  */
    private fun executeTestMatrix(): String {
        var testMatrix: TestMatrix? = null
        try {
            testMatrix = testMatrixCreate.execute()
        } catch (e: Exception) {
            try {
                testMatrix = testMatrixCreate.execute()
            } catch (e2: Exception) {
                try {
                    testMatrix = testMatrixCreate.execute()
                } catch (e3: Exception) {
                    fatalError(e3)
                }

            }

        }

        if (testMatrix == null) {
            throw IllegalStateException("test matrix is null")
        }

        return testMatrix.testMatrixId
    }

    private fun run() {
        val tryCount = 3

        var testMatrixId: String? = null
        var infrastructureFailure = false
        for (i in 0..tryCount - 1) {
            testMatrixId = executeTestMatrix()

            while (TestMatrixState.validatingOrPending(testMatrixId)) {
                sleep(20 * 1000)
            }

            if (TestMatrixState.infastructureFailure(testMatrixId)) {
                println("Retrying infrastructure failure")
                infrastructureFailure = true
            } else {
                infrastructureFailure = false
                break
            }
        }

        if (infrastructureFailure) {
            throw RuntimeException(
                    "Infrastructure failure on test matrix after 3x retry " + testMatrixId!!)
        }

        println("Successfully scheduled test matrix: " + testMatrixId!!)

        testMatrixIds.add(testMatrixId)
    }

    @Throws(Exception::class)
    override fun call() : Void {
        run()
        return Void.TYPE.newInstance()
    }
}
