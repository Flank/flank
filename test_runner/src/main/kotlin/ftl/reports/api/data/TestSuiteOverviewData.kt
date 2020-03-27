package ftl.reports.api.data

data class TestSuiteOverviewData(
    val total: Int,
    val errors: Int,
    val failures: Int,
    val flakes: Int,
    val skipped: Int,
    val elapsedTime: Double,
    val overheadTime: Double
)
