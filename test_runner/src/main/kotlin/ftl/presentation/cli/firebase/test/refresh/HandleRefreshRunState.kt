package ftl.presentation.cli.firebase.test.refresh

import ftl.config.FtlConstants
import ftl.domain.RefreshLastRunState

internal fun handleRefreshLastRunState(state: RefreshLastRunState): String = when (state) {
    is RefreshLastRunState.LoadingRun -> "Loading run ${state.lastRun}"
    is RefreshLastRunState.RefreshMatrices -> "${FtlConstants.indent}Refreshing ${state.matrixCount}x matrices"
    RefreshLastRunState.RefreshMatricesStarted -> "RefreshMatrices"
    is RefreshLastRunState.RefreshMatrix -> "${FtlConstants.indent} ${state.matrixState} ${state.matrixId}"
    RefreshLastRunState.UpdatingMatrixFile -> "${FtlConstants.indent}Updating matrix file"
}
