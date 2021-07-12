package ftl.analytics

import flank.tool.analytics.mixpanel.initializeStatisticsClient
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.util.isGoogleAnalyticsDisabled

internal fun IArgs.initUsageStatistics() {
    initializeStatisticsClient(
        disableUsageStatistics || isGoogleAnalyticsDisabled(flank.common.userHome),
        AndroidArgs::class,
        IosArgs::class,
        IArgs::class
    )
}
