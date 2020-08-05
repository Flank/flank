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
    commonArgs = commonArgs.copy(maxTestShards = convertToShardCount(commonArgs.maxTestShards)),
    xctestrunZip = gcloud.test?.processFilePath("from test").orEmpty(),
    xctestrunFile = gcloud.xctestrunFile?.processFilePath("from xctestrun-file").orEmpty(),
    xcodeVersion = gcloud.xcodeVersion,
    testTargets = flank.testTargets!!.filterNotNull()
)

private fun convertToShardCount(inputValue: Int) =
    if (inputValue != -1)
        inputValue else
        IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last
