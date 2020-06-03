package ftl.args

import ftl.config.IosConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig

fun createIosArgs(
    config: IosConfig
) = createIosArgs(
    gcloud = config.platform.gcloud,
    flank = config.platform.flank,
    commonArgs = config.common.createCommonArgs(config.data)
)

private fun createIosArgs(
    gcloud: IosGcloudConfig,
    flank: IosFlankConfig,
    commonArgs: CommonArgs
) = IosArgs(
    commonArgs = commonArgs,
    xctestrunZip = gcloud.test!!.processFilePath("from test"),
    xctestrunFile = gcloud.xctestrunFile!!.processFilePath("from xctestrun-file"),
    xcodeVersion = gcloud.xcodeVersion,
    testTargets = flank.testTargets!!.filterNotNull()
)
