package ftl.presentation.cli.firebase

import flank.common.newLine
import ftl.config.FtlConstants
import ftl.run.MatrixCancelStatus

internal fun MatrixCancelStatus.mapToMessage(): String = when (this) {
    is MatrixCancelStatus.NoMatricesToCancel -> "${FtlConstants.indent}No matrices to cancel$newLine"
    is MatrixCancelStatus.MatricesCanceled -> "${FtlConstants.indent}Cancelling ${count}x matrices$newLine"
}
