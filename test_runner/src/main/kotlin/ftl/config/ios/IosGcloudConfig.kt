package ftl.config.ios

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.Config
import ftl.args.yml.IYmlKeys
import ftl.args.yml.IYmlMap
import ftl.config.Device
import ftl.config.FtlConstants
import picocli.CommandLine

/**
 * iOS specific gcloud parameters
 *
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 * https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class IosGcloudConfig @JsonIgnore constructor(
    @JsonIgnore
    override val data: MutableMap<String, Any?>
) : Config {

    @set:CommandLine.Option(
        names = ["--test"],
        description = ["The path to the test package (a zip file containing the iOS app " +
                "and XCTest files). The given path may be in the local filesystem or in Google Cloud Storage using a URL " +
                "beginning with gs://. Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file " +
                "is specified."]
    )
    var test: String? by data

    @set:CommandLine.Option(
        names = ["--xctestrun-file"],
        description = ["The path to an .xctestrun file that will override any " +
                ".xctestrun file contained in the --test package. Because the .xctestrun file contains environment variables " +
                "along with test methods to run and/or ignore, this can be useful for customizing or sharding test suites. The " +
                "given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."]
    )
    @set:JsonProperty("xctestrun-file")
    var xctestrunFile: String? by data

    @set:CommandLine.Option(
        names = ["--xcode-version"],
        description = ["The version of Xcode that should be used to run an XCTest. " +
                "Defaults to the latest Xcode version supported in Firebase Test Lab. This Xcode version must be supported by " +
                "all iOS versions selected in the test matrix."]
    )
    @set:JsonProperty("xcode-version")
    var xcodeVersion: String? by data

    @CommandLine.Option(
        names = ["--device"],
        split = ",",
        description = ["A list of DIMENSION=VALUE pairs which specify a target " +
                "device to test against. This flag may be repeated to specify multiple devices. The four device dimensions are: " +
                "model, version, locale, and orientation. If any dimensions are omitted, they will use a default value. Omitting " +
                "all of the preceding dimension-related flags will run tests against a single device using defaults for all four " +
                "device dimensions."]
    )
    fun device(map: Map<String, String>?) {
        if (map.isNullOrEmpty()) return
        val androidDevice = Device(
            model = map.getOrDefault("model", FtlConstants.defaultIosModel),
            version = map.getOrDefault("version", FtlConstants.defaultIosVersion),
            locale = map.getOrDefault("locale", FtlConstants.defaultLocale),
            orientation = map.getOrDefault("orientation", FtlConstants.defaultOrientation)
        )

        if (device == null) device = mutableListOf()
        device?.add(androidDevice)
    }

    var device: MutableList<Device>? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys, IYmlMap {

        override val keys = listOf(
            "test",
            "xctestrun-file",
            "xcode-version",
            "device"
        )

        override val map = mapOf(
            "gcloud" to keys
        )

        fun default() = IosGcloudConfig().apply {
            test = null
            xctestrunFile = null
            xcodeVersion = null
            device = mutableListOf(Device(FtlConstants.defaultIosModel, FtlConstants.defaultIosVersion))
        }
    }
}
