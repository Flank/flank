package ftl.args

import ftl.config.IosConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig

fun createIosArgs(
    config: IosConfig? = null,
    gcloud: IosGcloudConfig = config!!.platform.gcloud,
    flank: IosFlankConfig = config!!.platform.flank,
    commonArgs: CommonArgs = config!!.common.createCommonArgs(config.data)
) = IosArgs(
    commonArgs = commonArgs,
    xctestrunZip = gcloud.test!!.processFilePath("from test"),
    xctestrunFile = gcloud.xctestrunFile!!.processFilePath("from xctestrun-file"),
    xcodeVersion = gcloud.xcodeVersion,
    devices = gcloud.device!!,
    testTargets = flank.testTargets!!.filterNotNull()
)
