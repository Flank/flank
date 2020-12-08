package ftl.config.ios

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.yml.IYmlKeys
import ftl.args.yml.ymlKeys
import ftl.config.Config
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
        description = [
            "The path to the test package (a zip file containing the iOS app " +
                "and XCTest files). The given path may be in the local filesystem or in Google Cloud Storage using a URL " +
                "beginning with gs://. Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file " +
                "is specified."
        ]
    )
    @set:JsonProperty("test")
    var test: String? by data

    @set:CommandLine.Option(
        names = ["--xctestrun-file"],
        description = [
            "The path to an .xctestrun file that will override any " +
                ".xctestrun file contained in the --test package. Because the .xctestrun file contains environment variables " +
                "along with test methods to run and/or ignore, this can be useful for customizing or sharding test suites. The " +
                "given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."
        ]
    )
    @set:JsonProperty("xctestrun-file")
    var xctestrunFile: String? by data

    @set:CommandLine.Option(
        names = ["--xcode-version"],
        description = [
            "The version of Xcode that should be used to run an XCTest. " +
                "Defaults to the latest Xcode version supported in Firebase Test Lab. This Xcode version must be supported by " +
                "all iOS versions selected in the test matrix."
        ]
    )
    @set:JsonProperty("xcode-version")
    var xcodeVersion: String? by data

    @set:CommandLine.Option(
        names = ["--additional-ipas"],
        split = ",",
        description = [
            "List of up to 100 additional IPAs to install, in addition to the one being directly tested. " +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation."
        ]
    )
    @set:JsonProperty("additional-ipas")
    var additionalIpas: List<String>? by data

    @set:CommandLine.Option(
        names = ["--app"],
        description = [
            "The path to the application archive (.ipa file) for game-loop testing. " +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation. " +
                "This flag is only valid when --type=game-loop is also set"
        ]
    )
    @set:JsonProperty("app")
    var app: String? by data

    @set:CommandLine.Option(
        names = ["--test-special-entitlements"],
        description = [
            "Enables testing special app entitlements. Re-signs an app having special entitlements with a new" +
                " application-identifier. This currently supports testing Push Notifications (aps-environment) entitlement " +
                "for up to one app in a project.\n" +
                "Note: Because this changes the app's identifier, make sure none of the resources in your zip file contain " +
                "direct references to the test app's bundle id."
        ]
    )
    @set:JsonProperty("test-special-entitlements")
    var testSpecialEntitlements: Boolean? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys {

        override val group = IYmlKeys.Group.GCLOUD

        override val keys by lazy {
            IosGcloudConfig::class.ymlKeys
        }

        fun default() = IosGcloudConfig().apply {
            test = null
            xctestrunFile = null
            xcodeVersion = null
            additionalIpas = emptyList()
            app = null
            testSpecialEntitlements = false
        }
    }
}
