package utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import flank.common.OutputReportCostNode

private val jsonMapper by lazy { JsonMapper().registerModule(KotlinModule()) }

fun String.asOutputReport() = jsonMapper.readValue<OutputReport>(this)

val OutputReport.firstTestSuiteOverview: SuiteOverview
    get() = testResults.values.first().testAxises.first().suiteOverview

data class OutputReport(
    val args: Any,
    val cost: OutputReportCostNode? = null,
    val weblinks: List<String> = emptyList(),
    @JsonProperty("test_results") val testResults: Map<String, Matrix> = emptyMap(),
    val error: String = ""
)

data class Matrix(
    val app: String = "",
    @JsonProperty("test-file")
    val testFile: String = "",
    @JsonProperty("test-axises")
    val testAxises: List<Outcome> = emptyList()
)

data class Outcome(
    val device: String = "",
    val outcome: String = "",
    val details: String = "",
    val suiteOverview: SuiteOverview = SuiteOverview()
)

data class SuiteOverview(
    val total: Int = 0,
    val errors: Int = 0,
    val failures: Int = 0,
    val flakes: Int = 0,
    val skipped: Int = 0,
    val elapsedTime: Double = 0.0,
    val overheadTime: Double = 0.0
)
