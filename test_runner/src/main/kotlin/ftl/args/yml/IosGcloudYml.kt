package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.Device
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion

/**
 * iOS specific gcloud parameters
 *
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 * https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class IosGcloudYmlParams(
    val test: String? = null,

    @field:JsonProperty("xctestrun-file")
    val xctestrunFile: String? = null,

    @field:JsonProperty("xcode-version")
    val xcodeVersion: String? = null,

    val device: List<Device> = listOf(Device(defaultIosModel, defaultIosVersion))
) {
    companion object : IYmlKeys {
        override val keys = listOf("test", "xctestrun-file", "xcode-version", "device")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class IosGcloudYml(
    val gcloud: IosGcloudYmlParams = IosGcloudYmlParams()
) {
    companion object : IYmlMap {
        override val map = mapOf("gcloud" to IosGcloudYmlParams.keys)
    }
}
