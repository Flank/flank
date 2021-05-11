package ftl.adapter.google

import ftl.api.TestMatrix
import ftl.client.google.BillableMinutes
import ftl.client.google.TestOutcome

fun Pair<BillableMinutes, List<TestOutcome>>.toApiModel(): TestMatrix.Summary {
    val (billableMinutes, outcomes) = this
    return TestMatrix.Summary(
        billableMinutes = billableMinutes.toApiModel(),
        axes = outcomes.map(TestOutcome::toApiModel)
    )
}

private fun BillableMinutes.toApiModel() = TestMatrix.BillableMinutes(virtual, physical)
