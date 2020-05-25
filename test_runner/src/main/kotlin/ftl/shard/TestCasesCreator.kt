package ftl.shard

import ftl.util.FlankTestMethod

data class TestMethod(
    val name: String,
    val time: Double
)

fun createTestCases(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>): List<TestMethod> {
    return testsToRun.map { TestMethod(it.testName, getTestMethodTime(it, previousMethodDurations)) }
}
