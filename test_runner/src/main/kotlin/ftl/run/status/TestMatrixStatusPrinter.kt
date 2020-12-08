package ftl.run.status

import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.log.logLn
import ftl.util.MatrixState
import ftl.util.StopWatch

class TestMatrixStatusPrinter(
    private val args: IArgs,
    testMatricesIds: Iterable<String>,
    private val stopWatch: StopWatch = StopWatch(),
    private val printExecutionStatusList: (String, List<TestExecution>?) -> Unit = ExecutionStatusListPrinter(args)
) : (TestMatrix) -> Unit {
    init {
        stopWatch.start()
    }

    private val cache = testMatricesIds.associateWith { MatrixState.PENDING }.toMutableMap()
    private val allMatricesCompleted get() = cache.values.all(MatrixState::completed)

    override fun invoke(matrix: TestMatrix) {
        val time = stopWatch.check(alignSeconds = true)
        printExecutionStatusList(time, matrix.testExecutions)
        cache[matrix.testMatrixId] = matrix.state
        if (allMatricesCompleted) printTestMatrixStatusList(time)
    }

    private fun printTestMatrixStatusList(time: String) {
        if (args.outputStyle == OutputStyle.Single) {
            logLn()
        }
        cache.forEach { (id, state) ->
            logLn("${FtlConstants.indent}$time $id $state")
        }
    }
}
