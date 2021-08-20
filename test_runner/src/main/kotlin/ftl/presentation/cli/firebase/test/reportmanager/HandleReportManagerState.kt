package ftl.presentation.cli.firebase.test.reportmanager

import flank.common.newLine
import flank.common.withNewLineAtTheEnd
import ftl.presentation.RunState
import ftl.reports.util.ReportManager
import kotlin.math.roundToInt

sealed interface ReportManagerState : RunState {
    data class Log(val message: String) : ReportManagerState
    data class Warning(val message: String) : ReportManagerState
    data class ShardTimes(val shards: List<ReportManager.ShardEfficiency>) : ReportManagerState
}

internal fun handleReportManagerState(state: ReportManagerState): String = when (state) {
    is ReportManagerState.Log -> state.message
    is ReportManagerState.Warning -> "WARNING: ${state.message}"
    is ReportManagerState.ShardTimes -> "Actual shard times:$newLine" + state.shards.joinToString(newLine) {
        "  ${it.shard}: Expected: ${it.expectedTime.roundToInt()}s, Actual: ${it.finalTime.roundToInt()}s, Diff: ${it.timeDiff.roundToInt()}s"
    }.withNewLineAtTheEnd()
}
