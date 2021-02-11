package flank.scripts.ops.dependencies

import flank.scripts.utils.failIfWindows

fun installXcPretty() {
    failIfWindows()
    downloadXcPrettyIfNeeded()
}
