@file:Suppress("UnusedPrivateClass")

package ftl.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ftl.args.ArgsHelper
import ftl.args.yml.YamlDeprecated.modifyAndThrow
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Path

fun loadAndroidConfig(
    path: Path? = null,
    reader: Reader = loadFile(path!!)
): AndroidConfig = modifyAndThrow(reader, android = true).run {
    AndroidConfig(
        data = this,
        common = parseYaml<CommonWrapper>().run { Config.Partial(gcloud, flank) },
        platform = parseYaml<AndroidWrapper>().run { Config.Partial(gcloud, flank) }
    )
}

fun loadIosConfig(
    path: Path? = null,
    reader: Reader = loadFile(path!!)
): IosConfig = modifyAndThrow(reader, android = false).run {
    IosConfig(
        data = this,
        common = parseYaml<CommonWrapper>().run { Config.Partial(gcloud, flank) },
        platform = parseYaml<IosWrapper>().run { Config.Partial(gcloud, flank) }
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
private class CommonWrapper(
    val flank: CommonFlankConfig = CommonFlankConfig(),
    val gcloud: CommonGcloudConfig = CommonGcloudConfig()
)

@JsonIgnoreProperties(ignoreUnknown = true)
private class AndroidWrapper(
    val flank: AndroidFlankConfig = AndroidFlankConfig(),
    val gcloud: AndroidGcloudConfig = AndroidGcloudConfig()
)

@JsonIgnoreProperties(ignoreUnknown = true)
private class IosWrapper(
    val flank: IosFlankConfig = IosFlankConfig(),
    val gcloud: IosGcloudConfig = IosGcloudConfig()
)

private inline fun <reified T : Any> String.parseYaml() =
    ArgsHelper.yamlMapper.readValue(this, T::class.java)
