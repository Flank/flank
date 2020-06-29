package ftl.reports.api.data

data class TestSuiteOverviewData(
    val total: Int,
    val errors: Int,
    val failures: Int,
    val flakes: Int,
    val skipped: Int,
    val elapsedTime: Double,
    val overheadTime: Double
) {
    operator fun plus(nextData: TestSuiteOverviewData?) =
        if (nextData == null) this else copy(
            total = this.total + nextData.total,
            errors = this.errors + nextData.errors,
            failures = this.failures + nextData.failures,
            flakes = this.flakes + nextData.flakes,
            skipped = this.skipped + nextData.skipped,
            elapsedTime = this.elapsedTime + nextData.elapsedTime,
            overheadTime = this.overheadTime + nextData.overheadTime
        )
}
