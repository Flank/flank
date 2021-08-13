package ftl.args

import ftl.args.yml.Type
import ftl.config.IosConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import ftl.util.require

fun createIosArgs(
    config: IosConfig,
    obfuscate: Boolean = false
) = createIosArgs(
    gcloud = config.platform.gcloud,
    flank = config.platform.flank,
    commonArgs = config.common.createCommonArgs(config.data),
    obfuscate = obfuscate
)

private fun createIosArgs(
    gcloud: IosGcloudConfig,
    flank: IosFlankConfig,
    commonArgs: CommonArgs,
    obfuscate: Boolean = false
) = IosArgs(
    commonArgs = commonArgs.copy(
        maxTestShards = convertToShardCount(commonArgs.maxTestShards),
        type = commonArgs.type ?: Type.XCTEST
    ),
    xctestrunZip = gcloud.test?.normalizeFilePath().orEmpty(),
    xctestrunFile = gcloud.xctestrunFile?.normalizeFilePath().orEmpty(),
    xcodeVersion = gcloud.xcodeVersion,
    additionalIpas = gcloud::additionalIpas.require().map { it.normalizeFilePath() },
    testTargets = flank.testTargets?.filterNotNull().orEmpty().parseEnvsIfNeeded(),
    obfuscateDumpShards = obfuscate,
    app = gcloud.app?.normalizeFilePath().orEmpty(),
    testSpecialEntitlements = gcloud.testSpecialEntitlements ?: false,
    onlyTestConfiguration = flank.onlyTestConfiguration.orEmpty(),
    skipTestConfiguration = flank.skipTestConfiguration.orEmpty()
)

private fun convertToShardCount(inputValue: Int) =
    if (inputValue != -1)
        inputValue else
        IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last
