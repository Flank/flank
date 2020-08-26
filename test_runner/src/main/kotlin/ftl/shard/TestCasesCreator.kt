package ftl.shard

import ftl.args.IArgs
import ftl.util.FlankTestMethod

data class TestMethod(
    val name: String,
    val time: Double,
    val isParameterized: Boolean = false
)

fun createTestCases(
    testsToRun: List<FlankTestMethod>,
    previousMethodDurations: Map<String, Double>,
    args: IArgs,
): List<TestMethod> {
    val defaultTestTime = args.fallbackTestTime(previousMethodDurations)
    return testsToRun
        .map {
        TestMethod(
            name = it.testName,
            time = getTestMethodTime(
                flankTestMethod = it,
                previousMethodDurations = previousMethodDurations,
                defaultTestTime = defaultTestTime
            ),
            isParameterized = it.classActually
        )
    }
}
