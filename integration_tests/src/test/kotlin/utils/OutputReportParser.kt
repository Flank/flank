package utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import flank.common.OutputReportCostNode

private val jsonMapper by lazy { JsonMapper().registerModule(KotlinModule()) }

fun String.asOutputReport() = jsonMapper.readValue<OutputReport>(this)

data class OutputReport(
    val args: Any,
    val cost: OutputReportCostNode? = null,
    val weblinks: List<String> = emptyList(),
    @JsonProperty("test_results") val testResults: Map<String, Matrix> = emptyMap(),
    val error: String = ""
)

data class Matrix(
    val app: String = "",
    @JsonProperty("test-axises") val testAxises: List<TextAxis>
)

data class TextAxis(
    val device: String,
    val outcome: String,
    val details: String,
    val testSuiteOverview: TestSuiteOverview
)

data class TestSuiteOverview(
    val total: Int,
    val errors: Int,
    val failures: Int,
    val flakes: Int,
    val skipped: Int,
    val elapsedTime: Double,
    val overheadTime: Double
)
