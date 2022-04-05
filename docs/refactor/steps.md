# Refactor steps

Steps below will be converted to a bunch of issues in epic scope.

## Move commands to correct package

*move to specified `package` the following `commands`*

1. `ftl.cli.firebase.test.android`
    * `AndroidLocalesCommand.kt`
    * `AndroidModelsCommand.kt`
    * `AndroidOrientationsCommand.kt`
    * `AndroidVersionsCommand.kt`
2. `ftl.cli.firebase.test.ios`
    * `IosLocalesCommand.kt`
    * `IosModelsCommand.kt`
    * `IosOrientationsCommand.kt`
    * `IosVersionsCommand.kt`

## Extract logic from CLI

1. Create package `ftl.domain` for domain layer
1. For each CLI command that is marked `? !`
    1. Create associated domain function in domain package.
    1. Move the logic from command run method to domain function.

### Commands to domain mappings:

* `LoginCommand -------------------> loginGoogleAccount`
* `AndroidLocalesDescribeCommand --> describeAndroidLocales`
* `AndroidLocalesListCommand ------> listAndroidLocales`
* `AndroidModelDescribeCommand ----> describeAndroidModels`
* `AndroidModelsListCommand -------> listAndroidModels`
* `AndroidOrientationsListCommand -> listAndroidOrientations`
* `AndroidVersionsDescribeCommand -> describeAndroidVersions`
* `AndroidVersionsListCommand -----> listAndroidVersions`
* `AndroidDoctorCommand -----------> runAndroidDoctor`
* `AndroidRunCommand --------------> runAndroidTest`
* `AndroidTestEnvironmentCommand --> describeAndroidTestEnvironment`
* `IosLocalesDescribeCommand ------> describeIosLocales`
* `IosLocalesListCommand ----------> listIosLocales`
* `IosModelDescribeCommand --------> describeIosModels`
* `IosModelsListCommand -----------> listIosModels`
* `IosOrientationsListCommand -----> listIosOrientations`
* `IosVersionsDescribeCommand -----> describeIosVersions`
* `IosVersionsListCommand ---------> listIosVersions`
* `IosDoctorCommand ---------------> runIosDoctor`
* `IosRunCommand ------------------> runIosTest`
* `IosTestEnvironmentCommand ------> describeIosTestEnvironment`
* `IPBlocksListCommand ------------> listIPBlocks`
* `NetworkProfilesDescribeCommand -> describeNetworkProfiles`
* `NetworkProfilesListCommand -----> listNetworkProfiles`
* `ProvidedSoftwareListCommand ----> listProvidedSoftware`
* `CancelCommand ------------------> cancelLastRun`
* `RefreshCommand -----------------> refreshLastRun`

### Files in domain package

```
ftl.domain
├── LoginGoogleAccount.kt
├── DescribeAndroidLocales.kt
├── ListAndroidLocales.kt
├── DescribeAndroidModels.kt
├── ListAndroidModels.kt
├── ListAndroidOrientations.kt
├── DescribeAndroidVersions.kt
├── ListAndroidVersions.kt
├── RunAndroidDoctor.kt
├── RunAndroidTest.kt
├── DescribeAndroidTestEnvironment.kt
├── DescribeIosLocales.kt
├── ListIosLocales.kt
├── DescribeIosModels.kt
├── ListIosModels.kt
├── ListIosOrientations.kt
├── DescribeIosVersions.kt
├── ListIosVersions.kt
├── RunIosDoctor.kt
├── RunIosTest.kt
├── DescribeIosTestEnvironment.kt
├── ListIPBlocks.kt
├── DescribeNetworkProfiles.kt
├── ListNetworkProfiles.kt
├── ListProvidedSoftware.kt
├── CancelLastRun.kt
└── RefreshLastRun.kt
```

## Use FileReference abstraction where possible

For convenience, all files that needs to be synchronized with the bucket, should treat as `FileReference` instead
of `String`.

### CommonArgs

* `otherFiles`

### AndroidArgs

* `appApk`
* `testApk`
* `additionalApks`
* `roboScript`
* `obbFiles`
* `additionalAppTestApks`

## Add packages for data layer

1. Add `ftl.interface`
1. Add `ftl.adapter`

## Create abstraction layer for external API

#### Important note!

Printing output to console is a part of presentation layer.

### Authorization

#### Target

`ftl/gc/UserAuth.kt`

#### Interface

`ftl/data/AuthorizeUser.kt`

```kotlin
package ftl.data

object UserAuthorization {
    interface Request : () -> UserAuthorization
}
```

### Provided software

#### Target

`ftl/environment/ProvidedSoftwareCatalog.kt`

#### Interface

`ftl/data/SoftwareCatalog.kt`

```kotlin
package ftl.data

data class SoftwareCatalog(
    val orchestratorVersion: String
) {
    interface Fetch : () -> SoftwareCatalog
}
```

#### Presentation

```kotlin
val softwareCatalogTable: suspend SoftwareCatalog.() -> String = TODO()
```

### IP Blocks List

#### Target

`ftl/environment/ListIPBlocks.kt`

#### Interface

`ftl/data/IpBlocks.kt`

```kotlin
package ftl.data

data class IpBlock(
    val block: String,
    val form: String,
    val addedDate: String
) {

    interface Fetch : () -> List<IpBlock>
}
```

#### Presentation

```kotlin
val ipBlocksTable: suspend List<IpBlock>.() -> String = TODO()
```

### Network profiles

#### Target

`ftl/environment/NetworkProfileDescription.kt`

#### Interface

`ftl/data/NetworkProfile.kt`

```kotlin
package ftl.data

data class NetworkProfile(
    val id: String,
    val downRule: Rule,
    val upRule: Rule
) {
    data class Rule(
        val bandwidth: String,
        val delay: String,
        val packetLossRatio: Float,
        val packetDuplicationRatio: Float,
        val burst: Float
    )

    interface Fetch : () -> List<NetworkProfile>
}
```

#### Presentation

```kotlin
val networkProfileDescription: suspend NetworkProfile.() -> String = TODO()
val networkProfileList: suspend List<NetworkProfile>.() -> String = TODO()
```

### Locales

#### Target

* `AndroidLocalesDescribeCommand` -> `AndroidCatalog/getLocaleDescription` -> `AndroidCatalog/getLocales`
* `AndroidLocalesListCommand` -> `AndroidCatalog/localesAsTable` -> `AndroidCatalog/getLocales`
* `IosLocalesDescribeCommand` -> `IosCatalog/getLocaleDescription` -> `IosCatalog/getLocales`
* `IosLocalesListCommand` -> `IosCatalog/localesAsTable` -> `IosCatalog/iosDeviceCatalog`

#### Interface

`ftl/data/Locales.kt`

```kotlin
package ftl.data

data class Locale(
    val id: String,
    val name: String,
    val region: String,
    val tags: List<String>,
) {

    data class Identity(
        val projectId: String,
        val platform: String,
    )

    interface Fetch : (Identity) -> List<Locale>
}
```

#### Presentation

```kotlin
val localeDescription: suspend Locale.() -> String = TODO()
val localeTable: suspend List<Locale>.() -> String = TODO()
```

### Device models

#### Target

* `AndroidModelDescribeCommand` -> `AndroidCatalog/describeModel` -> `AndroidCatalog/getModels`
* `AndroidModelsListCommand` -> `AndroidCatalog/devicesCatalogAsTable` -> `AndroidCatalog/getModels`
* `IosModelDescribeCommand` -> `IosCatalog/describeModel` -> `IosCatalog/getModels`
* `IosModelsListCommand` -> `IosCatalog/devicesCatalogAsTable` -> `IosCatalog/getModels`
* `AndroidArgs/validate` -> `AndroidArgs/assertDevicesSupported`
    * `AndroidCatalog/supportedDeviceConfig` -> `AndroidCatalog/deviceCatalog(projectId).models`
    * `AndroidCatalog/androidModelIds` -> `AndroidCatalog/deviceCatalog(projectId).models`
    * `AndroidCatalog/getSupportedVersionId` -> `AndroidCatalog/deviceCatalog(projectId).models`
* `IosArgs/validateRefresh` -> `IosArgs/assertDevicesSupported` -> `IosCatalog/iosDeviceCatalog(projectId).models`

#### Interface

`ftl/data/DeviceModels.kt`

```kotlin
package ftl.data

object DeviceModel {

    data class Android(
        val id: String,
        val name: String,
        val tags: List<String>,
        val screenX: Int,
        val screenY: Int,
        val formFactor: String,
        val screenDensity: Int,
        val supportedVersionIds: List<String>,
        val form: String,
        val brand: String,
        val codename: String,
        val manufacturer: String,
        val thumbnailUrl: String,
        val supportedAbis: List<String>,
        val lowFpsVideoRecording: Boolean,
    ) {

        interface Fetch : (projectId: String) -> List<Android>
    }

    data class Ios(
        val id: String,
        val name: String,
        val tags: List<String>,
        val screenX: Int,
        val screenY: Int,
        val formFactor: String,
        val screenDensity: Int,
        val supportedVersionIds: List<String>,
        val deviceCapabilities: List<String>,
    ) {

        interface Fetch : (projectId: String) -> List<Ios>
    }
}
```

#### Presentation

```kotlin
val androidModelDescription: suspend DeviceModel.Android.() -> String = TODO()
val androidModelsTable: suspend List<DeviceModel.Android>.() -> String = TODO()
val iosModelDescription: suspend DeviceModel.Ios.() -> String = TODO()
val iosModelsTable: suspend List<DeviceModel.Ios>.() -> String = TODO()
```

### Orientation

#### Target

* `AndroidOrientationsListCommand` -> `AndroidCatalog/supportedOrientationsAsTable` -> `AndroidCatalog/deviceCatalog`
* `IosOrientationsListCommand` -> `IosCatalog/supportedOrientationsAsTable` -> `IosCatalog/iosDeviceCatalog`

#### Interface

`ftl/data/Orientation.kt`

```kotlin
package ftl.data

data class Orientation(
    val id: String,
    val name: String,
    val tags: String,
) {
    interface Fetch : (projectId: String, platform: String) -> List<Orientation>
}
```

#### Presentation

```kotlin
val orientationsTable: suspend List<Orientation>.() -> String = TODO()
```

### OS Version

#### Target

* `AndroidVersionsListCommand` -> `AndroidCatalog/supportedVersionsAsTable` -> `AndroidCatalog/getVersionsList`
* `IosVersionsListCommand` -> `IosCatalog/softwareVersionsAsTable` -> `IosCatalog/getVersionsList`
* `AndroidArgs/validate` -> `AndroidArgs/assertDevicesSupported` -> `AndroidCatalog/androidVersionIds` -> `AndroidCatalog/deviceCatalog(projectId).versions`

#### Interface

`ftl/data/OsVersion.kt`

```kotlin
package ftl.data

object OsVersion {

    data class Android(
        val apiLevel: Int,
        val codeName: String,
        val distribution: Distribution,
        val id: String,
        val releaseDate: Date,
        val tags: List<String>,
        val versionString: String,
    ) {
        interface Fetch : (projectId: String) -> List<Android>
    }

    data class Ios(
        val id: String,
        val majorVersion: Int,
        val minorVersion: Int,
        val supportedXcodeVersionIds: List<String>,
        val tags: List<String>,
    ) {
        interface Fetch : (projectId: String) -> List<Ios>
    }
}
```

#### Presentation

```kotlin
val androidOsVersionDescription: suspend OsVersion.Android.() -> String = TODO()
val androidOsVersionsTable: suspend List<OsVersion.Android>.() -> String = TODO()
val iosOsVersionDescription: suspend OsVersion.Ios.() -> String = TODO()
val iosOsVersionsTable: suspend List<OsVersion.Ios>.() -> String = TODO()
```

### File reference

#### Target

* `FileReference`

#### Interface

`ftl/data/FileReference.kt`

```kotlin
data class FileReference(
    val local: String = "",
    val remote: String = ""
) {
    interface Exist : (FileReference) -> Boolean
    interface Upload : (FileReference) -> FileReference
    interface Download : (FileReference) -> FileReference
}
```

### Remote storage aka GcStorage

#### Target

* `IArgs/checkResultsDirUnique`

#### Interface

`ftl/data/RemoteStorage.kt`

```kotlin
package ftl.data

object RemoteStorage {

    data class Dir(
        val bucket: String,
        val path: String
    )

    class Data(
        val path: String,
        val bytes: ByteArray? = null // Use, when file under the given path doesn't exist.
    )

    interface Exist : (Dir) -> Boolean

    interface Upload : (Dir, Data) -> Unit
}
```

### Test matrix results, cancel & refresh

#### Target

* `SavedMatrix`
* `TestOutcome`
* `TestSuiteOverviewData`
* `MatrixMap`
* `CancelCommand` -> `cancelLastRun` -> `cancelMatrices` -> `GcTestMatrix/cancel`
* `RefreshCommand` -> `refreshLastRun`
    * `refreshMatrices`
        * `GcTestMatrix/refresh`
        * `SavedMatrix/updateWithMatrix` -> `SavedMatrix/updatedSavedMatrix` -> `TestMatrix/fetchTestOutcomeContext`
* `newTestRun`
    * `pollMatrices` -> `matrixChangesFlow` -> `GcTestMatrix/refresh`
    * `Iterable<TestMatrix>/updateMatrixMap` -> `SavedMatrix/updateWithMatrix` -> `TestMatrix/fetchTestOutcomeContext`
    * `ReportManager/generate`
        * `ReportManager/parseTestSuite`
            * `refreshMatricesAndGetExecutions` -> `refreshTestMatrices` -> `GcTestMatrix/refresh`

#### Interface

`ftl/data/TestMatrix.kt`

```kotlin
object TestMatrix {

    data class Result(
        val runPath: String,
        val map: Map<String, Data>,
    )

    data class Data(
        val matrixId: String = "",
        val state: String = "",
        val gcsPath: String = "",
        val webLink: String = "",
        val downloaded: Boolean = false,
        val billableMinutes: BillableMinutes = BillableMinutes(),
        val clientDetails: Map<String, String>? = null,
        val gcsPathWithoutRootBucket: String = "",
        val gcsRootBucket: String = "",
        val axes: List<Outcome> = emptyList()
    )

    data class Outcome(
        val device: String = "",
        val outcome: String = "",
        val details: String = "",
        val suiteOverview: SuiteOverview = SuiteOverview()
    )

    data class SuiteOverview(
        val total: Int = 0,
        val errors: Int = 0,
        val failures: Int = 0,
        val flakes: Int = 0,
        val skipped: Int = 0,
        val elapsedTime: Double = 0.0,
        val overheadTime: Double = 0.0
    )

    data class BillableMinutes(
        val virtual: Long = 0,
        val physical: Long = 0
    )

    data class Summary(
        val billableMinutes: BillableMinutes,
        val axes: List<Outcome>,
    ) {
        data class Identity(
            val projectId: String,
            val historyId: String,
            val executionId: String,
        )

        interface Fetch : (Identity) -> Summary
    }

    data class Identity(
        val matrixId: String,
        val projectId: String,
    )

    interface Cancel : (Identity) -> Unit
    interface Refresh : (Identity) -> Data
}
```

#### Presentation

```kotlin
TODO()
```

### Execute Android tests matrix

#### Target

* `AndroidRunCommand` -> `AndroidArgs/runAndroidTests` -> `GcAndroidTestMatrix/build`
* `AndroidTestConfig`

#### Interface

`ftl/data/AndroidTestMatrix.kt`

```kotlin
package ftl.data

object AndroidTestMatrix {

    data class Config(
        // args
        val clientDetails: Map<String, String>?,
        val resultsBucket: String,
        val autoGoogleLogin: Boolean,
        val networkProfile: String?,
        val directoriesToPull: List<String>,
        val obbNames: List<String>,
        val environmentVariables: Map<String, String>,
        val autograntPermissions: Boolean,
        val testTimeout: String,
        val performanceMetrics: Boolean,
        val recordVideo: Boolean,
        val flakyTestAttempts: Int,
        val failFast: Boolean,
        val project: String,
        val resultsHistoryName: String?,

        // build
        val otherFiles: Map<String, String>,
        val runGcsPath: String,
        val devices: List<Device>,
        val additionalApkGcsPaths: List<String>,
        val obbFiles: Map<String, String>,
    )

    sealed class Type {
        data class Instrumentation(
            val appApkGcsPath: String,
            val testApkGcsPath: String,
            val testRunnerClass: String?,
            val orchestratorOption: String?,
            // sharding
            val disableSharding: Boolean,
            val testShards: ShardChunks,
            val numUniformShards: Int?,
            val keepTestTargetsEmpty: Boolean,
            val environmentVariables: Map<String, String> = emptyMap(),
            val testTargetsForShard: ShardChunks
        ) : Type()

        data class Robo(
            val appApkGcsPath: String,
            val flankRoboDirectives: List<FlankRoboDirective>?,
            val roboScriptGcsPath: String?
        ) : Type()

        data class GameLoop(
            val appApkGcsPath: String,
            val testRunnerClass: String?,
            val scenarioNumbers: List<String>,
            val scenarioLabels: List<String>
        ) : Type()
    }

    interface Execute : (Config, Type) -> TestMatrix.Result
}
```

#### Presentation

```kotlin
TODO()
```

### Execute Ios tests matrix

#### Target

* `IosRunCommand` -> `IosArgs/runIosTests` -> `GcIosTestMatrix/build`
* `IosTestContext`

#### Interface

`ftl/data/IosTestMatrix.kt`

```kotlin
package ftl.data

object IosTestMatrix {

    data class Config(
        // args
        val clientDetails: Map<String, String>?,
        val networkProfile: String?,
        val directoriesToPull: List<String>,
        val testTimeout: String,
        val recordVideo: Boolean,
        val flakyTestAttempts: Int,
        val failFast: Boolean,
        val project: String,
        val resultsHistoryName: String?,

        // build
        val devices: List<Device>,
        val otherFiles: Map<String, String>,
        val additionalIpasGcsPaths: List<String>,
    )

    sealed class Type {
        data class XcTest(
            val xcTestGcsPath: String,
            val xcTestRunFileGcsPath: String,
            val xcodeVersion: String,
            val testSpecialEntitlements: Boolean,
            val matrixGcsPath: String,
        )

        data class GameLoop(
            val appGcsPath: String,
            val scenarios: List<Int>,
            val matrixGcsPath: String,
        )
    }

    interface Execute : (Config, Type) -> TestMatrix.Result
}
```

#### Presentation

```kotlin
TODO()
```

### JUnit results

#### Target

* `ReportManager/generate` -> `ReportManager/parseTestSuite`
    * `ReportManager/processXmlFromFile`
    * `refreshMatricesAndGetExecutions` -> `List<TestExecution>/createJUnitTestResult`

#### Interface

`ftl/data/JUnitTestResult.kt`

```kotlin
package ftl.data

object JUnitTest {

    @JacksonXmlRootElement(localName = "testsuites")
    data class Result(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JacksonXmlProperty(localName = "testsuite")
        var testsuites: MutableList<Suite>? = null
    ) {
        data class ApiIdentity(
            val projectId: String,
            val matrixIds: List<String>
        )
        interface GenerateFromApi : (ApiIdentity) -> Result

        interface ParseFromFiles : (File) -> Result
    }

    data class Suite(
        @JacksonXmlProperty(isAttribute = true)
        var name: String,

        @JacksonXmlProperty(isAttribute = true)
        var tests: String, // Int

        @JacksonXmlProperty(isAttribute = true)
        var failures: String, // Int

        @JacksonXmlProperty(isAttribute = true)
        var flakes: Int? = null,

        @JacksonXmlProperty(isAttribute = true)
        var errors: String, // Int

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(isAttribute = true)
        var skipped: String?, // Int. Android only

        @JacksonXmlProperty(isAttribute = true)
        var time: String, // Double

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(isAttribute = true)
        val timestamp: String?, // String. Android only

        @JacksonXmlProperty(isAttribute = true)
        val hostname: String? = "localhost", // String.

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(isAttribute = true)
        val testLabExecutionId: String? = null, // String.

        @JacksonXmlProperty(localName = "testcase")
        var testcases: MutableCollection<Case>?,

        // not used
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val properties: Any? = null, // <properties />

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "system-out")
        val systemOut: Any? = null, // <system-out />

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "system-err")
        val systemErr: Any? = null // <system-err />
    )

    data class Case(
        // name, classname, and time are always present except for empty test cases <testcase/>
        @JacksonXmlProperty(isAttribute = true)
        val name: String?,

        @JacksonXmlProperty(isAttribute = true)
        val classname: String?,

        @JacksonXmlProperty(isAttribute = true)
        val time: String?,

        // iOS contains multiple failures for a single test.
        // JUnit XML allows arbitrary amounts of failure/error tags
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "failure")
        val failures: List<String>? = null,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "error")
        val errors: List<String>? = null,

        @JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FilterNotNull::class)
        val skipped: String? = "absent" // used by FilterNotNull to filter out absent `skipped` values
    ) {

        // Consider to move all properties to constructor if will doesn't conflict with parser

        @JsonInclude(JsonInclude.Include.NON_NULL)
        var webLink: String? = null

        @JacksonXmlProperty(isAttribute = true)
        var flaky: Boolean? = null // use null instead of false
    }

    @Suppress("UnusedPrivateClass")
    private class FilterNotNull {
        override fun equals(other: Any?): Boolean {
            // other is null     = present
            // other is not null = absent (default value)
            return other != null
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}


```

### Performance Metrics

#### Target

`GcToolResults/getPerformanceMetric`

#### Interface

`ftl/data/PerfMetrics.kt`

```kotlin
package ftl.data

object PerfMetrics {

    // based on com.google.api.services.toolresults.model.PerfMetricsSummary
    data class Summary(
        val appStartTime: AppStartTime? = null,
        val graphicsStats: GraphicsStats? = null,
        val perfEnvironment: PerfEnvironment? = null,
        val perfMetrics: List<String>? = null,
        val executionId: String? = null,
        val historyId: String? = null,
        val projectId: String? = null,
        val stepId: String? = null,
    )

    data class GraphicsStats(
        val buckets: List<Bucket>? = null,
        val highInputLatencyCount: Long? = null,
        val jankyFrames: Long? = null,
        val missedVsyncCount: Long? = null,
        val p50Millis: Long? = null,
        val p90Millis: Long? = null,
        val p95Millis: Long? = null,
        val p99Millis: Long? = null,
        val slowBitmapUploadCount: Long? = null,
        val slowDrawCount: Long? = null,
        val slowUiThreadCount: Long? = null,
        val totalFrames: Long? = null,
    ) {
        data class Bucket(
            val frameCount: Long?,
            val renderMillis: Long?,
        )
    }

    data class AppStartTime(
        val fullyDrawnTime: Duration? = null,
        val initialDisplayTime: Duration? = null,
    )

    data class PerfEnvironment(
        val cpuInfo: CPUInfo? = null,
        val memoryInfo: MemoryInfo? = null,
    )

    data class CPUInfo(
        val cpuProcessor: String? = null,
        val cpuSpeedInGhz: Float? = null,
        val numberOfCores: Int? = null,
    )

    data class MemoryInfo(
        val memoryCapInKibibyte: Long? = null,
        val memoryTotalInKibibyte: Long? = null,
    )

    data class Identity(
        val executionId: String,
        val historyId: String,
        val projectId: String,
        val stepId: String,
    )

    interface Fetch : (Identity) -> Summary
}
```

### Fetching Artifacts

#### Target

* `FetchArtifacts.kt`

#### Interface

`ftl/data/PerfMetrics.kt`

```kotlin
package ftl.data

object Artifacts {

    data class Identity(
        val gcsPathWithoutRootBucket: String,
        val gcsRootBucket: String,
        val regex: List<Regex>,
        val blobPath: String,
        val downloadPath: DownloadPath,
    )

    data class DownloadPath(
        val localResultDir: String,
        val useLocalResultDir: Boolean,
        val keepFilePath: Boolean,
    )

    interface Fetch: (Identity) -> List<String>
}
```
