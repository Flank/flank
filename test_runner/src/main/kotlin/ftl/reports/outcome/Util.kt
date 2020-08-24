package ftl.reports.outcome

import com.google.api.client.json.GenericJson
import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import ftl.environment.orUnknown
import ftl.util.mutableMapProperty

internal fun Step.deviceModel() = dimensionValue.deviceModel()

internal fun Environment.deviceModel() = dimensionValue.deviceModel()

private fun List<GenericJson>?.deviceModel() = this
    ?.toDimensionMap()
    ?.getValues(dimensionKeys)
    ?.joinToString("-")
    .orUnknown()

private fun List<GenericJson>.toDimensionMap(): Map<String?, String?> = associate { it.key to it.value }

private fun Map<String?, String?>.getValues(keys: Iterable<String>) = keys.mapNotNull { key -> get(key) }

private val GenericJson.key: String? by mutableMapProperty { null }

private val GenericJson.value: String? by mutableMapProperty { null }

private val dimensionKeys = DimensionValues.values().map(DimensionValues::name)

private enum class DimensionValues { Model, Version, Locale, Orientation }
