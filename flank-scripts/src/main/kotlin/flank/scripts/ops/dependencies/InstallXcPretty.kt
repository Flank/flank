package flank.scripts.ops.dependencies

import flank.scripts.ops.common.downloadXcPrettyIfNeeded
import flank.scripts.utils.failIfWindows

fun installXcPretty() {
    failIfWindows()
    downloadXcPrettyIfNeeded()
}
