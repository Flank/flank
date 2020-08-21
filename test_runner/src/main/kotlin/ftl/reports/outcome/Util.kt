package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.EnvironmentDimensionValueEntry
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.StepDimensionValueEntry
import ftl.environment.orUnknown

internal fun Step.deviceModel() = dimensionValue["Model"]
    ?.value.orUnknown()

internal fun Environment.deviceModel() = dimensionValue["Model"]
    ?.value.orUnknown()

operator fun List<StepDimensionValueEntry>?.get(key: String) =
    this?.firstOrNull { it.key == key }

operator fun List<EnvironmentDimensionValueEntry>?.get(key: String) =
    this?.firstOrNull { it.key == key }
