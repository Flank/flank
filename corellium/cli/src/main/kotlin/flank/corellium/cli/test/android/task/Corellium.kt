package flank.corellium.cli.test.android.task

import flank.corellium.api.CorelliumApi
import flank.corellium.cli.TestAndroidCommand.Companion.context
import flank.corellium.cli.TestAndroidCommand.Config
import flank.corellium.corelliumApi
import flank.exection.parallel.from
import flank.exection.parallel.type
import flank.exection.parallel.using

internal val corelliumApi = type<CorelliumApi>() from setOf(Config) using context {
    corelliumApi(config.project!!)
}
