package ftl.reports.outcome

import com.google.api.services.toolresults.model.Step
import ftl.environment.orUnknown

internal fun Step.deviceModel() = dimensionValue["Model"]
    ?.value.orUnknown()
