package ftl.config

import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import picocli.CommandLine

interface Config {
    val data: MutableMap<String, Any?>

    @CommandLine.Command
    data class Partial<Gcloud : Config, Flank : Config>(
        @field:CommandLine.Mixin
        val gcloud: Gcloud,
        @field:CommandLine.Mixin
        val flank: Flank
    )

    @CommandLine.Command
    data class Platform<Gcloud : Config, Flank : Config>(
        val data: String = "",
        @field:CommandLine.Mixin
        val common: Partial<CommonGcloudConfig, CommonFlankConfig>,
        @field:CommandLine.Mixin
        val platform: Partial<Gcloud, Flank>
    )
}

typealias CommonConfig = Config.Partial<CommonGcloudConfig, CommonFlankConfig>
typealias AndroidConfig = Config.Platform<AndroidGcloudConfig, AndroidFlankConfig>
typealias IosConfig = Config.Platform<IosGcloudConfig, IosFlankConfig>
