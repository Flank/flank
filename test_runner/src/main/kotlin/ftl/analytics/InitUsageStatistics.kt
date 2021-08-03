package ftl.analytics

import flank.tool.analytics.mixpanel.Mixpanel
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs

internal fun IArgs.initUsageStatistics() {
    Mixpanel.configure(
        projectName = project,
        blockUsageStatistics = disableUsageStatistics,
        AndroidArgs::class,
        IosArgs::class,
        IArgs::class
    )
}
