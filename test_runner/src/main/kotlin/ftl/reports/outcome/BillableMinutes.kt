package ftl.reports.outcome

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.StepDimensionValueEntry
import ftl.android.AndroidCatalog
import ftl.util.billableMinutes
import kotlin.math.min

data class BillableMinutes(
    val virtual: Long = 0,
    val physical: Long = 0
)

fun List<Step>.calculateAndroidBillableMinutes(
    projectId: String,
    timeoutValue: Long
): BillableMinutes =
    groupByDeviceType(projectId).run {
        BillableMinutes(
            virtual = getValue(true).sumBillableMinutes(timeoutValue),
            physical = getValue(false).sumBillableMinutes(timeoutValue)
        )
    }

private fun List<Step>.groupByDeviceType(projectId: String) =
    groupBy {
        AndroidCatalog.isVirtualDevice(
            it.deviceModel(),
            projectId
        )
    }

private fun List<Step>.sumBillableMinutes(timeout: Long) =
    mapNotNull { step ->
        step.getBillableSeconds(default = timeout)
    }.map {
        billableMinutes(it)
    }.sum()

private fun Step.getBillableSeconds(default: Long) =
    testExecutionStep?.testTiming?.testProcessDuration?.seconds?.let {
        min(it, default)
    }

operator fun List<StepDimensionValueEntry>.get(key: String) = 
    firstOrNull { it.key == key }
