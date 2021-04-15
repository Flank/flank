package ftl.reports.outcome

import com.google.api.client.json.GenericJson
import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import ftl.adapter.environment.orUnknown
import ftl.util.mutableMapProperty

internal fun Step.axisValue() = dimensionValue.axisValue()

internal fun Environment.axisValue() = dimensionValue.axisValue()

private fun List<GenericJson>?.axisValue() = this
    ?.toDimensionMap()
    ?.getValues(dimensionKeys)
    ?.joinToString("-")
    .orUnknown()

private fun List<GenericJson>.toDimensionMap(): Map<String?, String?> = associate { it.key to it.value }

private fun Map<String?, String?>.getValues(keys: Iterable<String>) = keys.mapNotNull { key -> get(key) }

private val GenericJson.key: String? by mutableMapProperty { null }

private val GenericJson.value: String? by mutableMapProperty { null }

private val dimensionKeys = DimensionValue.values().map(DimensionValue::name)

private enum class DimensionValue { Model, Version, Locale, Orientation }
