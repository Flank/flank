package ftl.domain

import ftl.args.AndroidArgs
import ftl.run.cancelLastRun

interface CancelLastRun

fun CancelLastRun.invoke() {
    cancelLastRun(AndroidArgs.default())
}
