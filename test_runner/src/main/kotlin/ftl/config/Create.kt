package ftl.config

import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import ftl.presentation.cli.firebase.test.android.AndroidRunCommand
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import kotlin.properties.ReadOnlyProperty

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

fun createConfiguration(
    gcloudConfig: IosGcloudConfig,
    flankConfig: IosFlankConfig
) = ReadOnlyProperty<IosRunCommand, IosConfig> { iosRunCommand, _ ->
    IosConfig(
        common = iosRunCommand.commonConfig,
        platform = Config.Partial(
            gcloud = gcloudConfig,
            flank = flankConfig
        )
    )
}

fun createConfiguration(
    gcloudConfig: AndroidGcloudConfig,
    flankConfig: AndroidFlankConfig
) = ReadOnlyProperty<AndroidRunCommand, AndroidConfig> { androidRunCommand, _ ->
    AndroidConfig(
        common = androidRunCommand.commonConfig,
        platform = Config.Partial(
            gcloud = gcloudConfig,
            flank = flankConfig
        )
    )
}
