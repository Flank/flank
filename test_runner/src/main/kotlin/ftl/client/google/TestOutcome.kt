package ftl.client.google

import ftl.reports.api.data.TestSuiteOverviewData

data class TestOutcome(
    val device: String = "",
    val outcome: String = "",
    val details: String = "",
    val testSuiteOverview: TestSuiteOverviewData = TestSuiteOverviewData()
)
