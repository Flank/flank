package ftl.config

import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig

fun defaultAndroidConfig() = AndroidConfig(
    common = Config.Partial(
        gcloud = CommonGcloudConfig.default(android = true),
        flank = CommonFlankConfig.default()
    ),
    platform = Config.Partial(
        gcloud = AndroidGcloudConfig.default(),
        flank = AndroidFlankConfig.default()
    )
)

fun defaultIosConfig() = IosConfig(
    common = Config.Partial(
        gcloud = CommonGcloudConfig.default(android = false),
        flank = CommonFlankConfig.default()
    ),
    platform = Config.Partial(
        gcloud = IosGcloudConfig.default(),
        flank = IosFlankConfig.default()
    )
)

fun emptyAndroidConfig() = AndroidConfig(
    common = Config.Partial(
        gcloud = CommonGcloudConfig(),
        flank = CommonFlankConfig()
    ),
    platform = Config.Partial(
        gcloud = AndroidGcloudConfig(),
        flank = AndroidFlankConfig()
    )
)

fun emptyIosConfig() = IosConfig(
    common = Config.Partial(
        gcloud = CommonGcloudConfig(),
        flank = CommonFlankConfig()
    ),
    platform = Config.Partial(
        gcloud = IosGcloudConfig(),
        flank = IosFlankConfig()
    )
)
