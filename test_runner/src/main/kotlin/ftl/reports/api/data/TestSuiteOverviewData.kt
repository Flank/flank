package ftl.reports.api.data

import com.google.api.services.toolresults.model.TestSuiteOverview
import ftl.reports.api.millis

data class TestSuiteOverviewData(
    val total: Int = 0,
    val errors: Int = 0,
    val failures: Int = 0,
    val flakes: Int = 0,
    val skipped: Int = 0,
    val elapsedTime: Double = 0.0,
) {
    operator fun plus(nextData: TestSuiteOverviewData?) =
        if (nextData == null) this else copy(
            total = this.total + nextData.total,
            errors = this.errors + nextData.errors,
            failures = this.failures + nextData.failures,
            flakes = this.flakes + nextData.flakes,
            skipped = this.skipped + nextData.skipped,
            elapsedTime = this.elapsedTime + nextData.elapsedTime,
        )

    operator fun plus(data: TestSuiteOverview) = copy(
        total = total + (data.totalCount ?: 0),
        errors = errors + (data.errorCount ?: 0),
        failures = failures + (data.failureCount ?: 0),
        flakes = flakes + (data.flakyCount ?: 0),
        skipped = skipped + (data.skippedCount ?: 0),
        elapsedTime = elapsedTime + data.elapsedTime.millis()
    )
}
