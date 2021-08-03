package ftl.analytics

import flank.common.userHome
import flank.tool.analytics.mixpanel.Mixpanel
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.util.isGoogleAnalyticsDisabled

internal fun IArgs.initUsageStatistics() {
    Mixpanel.configure(project)
    Mixpanel.initializeStatisticsClient(
        disableUsageStatistics || isGoogleAnalyticsDisabled(userHome),
        AndroidArgs::class,
        IosArgs::class,
        IArgs::class
    )
}
