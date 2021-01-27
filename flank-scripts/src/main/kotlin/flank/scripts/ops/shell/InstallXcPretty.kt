package flank.scripts.ops.shell

import flank.scripts.utils.downloadXcPrettyIfNeeded
import flank.scripts.utils.failIfWindows

fun installXcPretty() {
    failIfWindows()
    downloadXcPrettyIfNeeded()
}
