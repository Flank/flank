package ftl.domain

import ftl.args.AndroidArgs
import ftl.presentation.Output
import ftl.run.cancelLastRun

interface CancelLastRun : Output

fun CancelLastRun.invoke() {
    cancelLastRun(AndroidArgs.default()).out()
}
