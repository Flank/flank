package ftl.reports.outcome

import com.google.api.services.toolresults.model.Step

internal fun Step.deviceModel() = dimensionValue["Model"]
    ?.value
    ?: "unknown"
