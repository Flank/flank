package ftl.shard

import ftl.util.FlankTestMethod

data class TestMethod(
    val name: String,
    val time: Double
)

object TestCasesCreator {
    fun createTestCases(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>): List<TestMethod> {
        return testsToRun
            .map {
                TestMethod(
                    name = it.testName,
                    time = if (it.ignored) {
                        IGNORE_TEST_TIME
                    } else {
                        previousMethodDurations[it.testName] ?: DEFAULT_TEST_TIME_SEC
                    }
                )
            }
    }
}
