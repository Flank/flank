package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.adapter.google.toClientModel
import ftl.api.PerfMetrics
import ftl.client.google.GcToolResults

object GooglePerformanceMetricsFetch :
    PerfMetrics.Fetch,
    (PerfMetrics.Identity) -> PerfMetrics.Summary by { identity ->
        GcToolResults.getPerformanceMetric(identity.toClientModel()).toApiModel()
    }
