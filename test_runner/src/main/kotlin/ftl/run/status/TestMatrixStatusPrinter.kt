package ftl.run.status

import flank.common.logLn
import ftl.api.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.reports.addStepTime
import ftl.util.MatrixState
import ftl.util.StopWatch
import ftl.util.formatted

class TestMatrixStatusPrinter(
    private val args: IArgs,
    testMatricesIds: Iterable<String>,
    private val stopWatch: StopWatch = StopWatch(),
    private val printExecutionStatusList: (String, List<TestMatrix.TestExecution>) -> Unit = ExecutionStatusListPrinter(args)
) : (TestMatrix.Data) -> Unit {
    init {
        stopWatch.start()
    }

    private val cache = testMatricesIds.associateWith { MatrixState.PENDING }.toMutableMap()
    private val allMatricesCompleted get() = cache.values.all(MatrixState::completed)

    override fun invoke(matrix: TestMatrix.Data) {
        val time = stopWatch.check()
        printExecutionStatusList(time.formatted(alignSeconds = true), matrix.testExecutions)
        cache[matrix.matrixId] = matrix.state
        if (allMatricesCompleted) {
            printTestMatrixStatusList(time.formatted(alignSeconds = true))
            addStepTime("Executing matrices", time)
        }
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
