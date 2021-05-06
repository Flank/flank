package ftl.client.google

import com.google.api.services.toolresults.model.Step
import ftl.environment.orUnknown
import ftl.util.billableMinutes
import kotlin.math.min

data class BillableMinutes(
    val virtual: Long = 0,
    val physical: Long = 0
)

fun List<Step>.calculateAndroidBillableMinutes(projectId: String, timeoutValue: Long): BillableMinutes = this
    .groupBy { deviceType(it.deviceModel(), projectId) }
    .run {
        BillableMinutes(
            virtual = get(DeviceType.VIRTUAL)?.sumBillableMinutes(timeoutValue) ?: 0,
            physical = get(DeviceType.PHYSICAL)?.sumBillableMinutes(timeoutValue) ?: 0
        )
    }

private fun Step.deviceModel() = dimensionValue.find { it.key.equals("Model", ignoreCase = true) }?.value.orUnknown()

private fun List<Step>.sumBillableMinutes(timeout: Long) = this
    .mapNotNull { it.getBillableSeconds(default = timeout) }
    .map { billableMinutes(it) }
    .sum()

private fun Step.getBillableSeconds(default: Long) =
    testExecutionStep?.testTiming?.testProcessDuration?.seconds?.let {
        min(it, default)
    }

private fun deviceType(modelId: String, projectId: String) =
    if (AndroidCatalog.isVirtualDevice(modelId, projectId)) DeviceType.VIRTUAL
    else DeviceType.PHYSICAL
