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

For convenience, all files that needs to be synchronized with the bucket,
should treat as `FileReference` instead of `String`.

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

`ftl/interfaces/AuthorizeUser.kt`

```kotlin
package ftl.interfaces

object UserAuthorization {
    interface Request : () -> UserAuthorization
}
```

### Provided software

#### Target

`ftl/environment/ProvidedSoftwareCatalog.kt`

#### Interface

`ftl/interfaces/SoftwareCatalog.kt`

```kotlin
package ftl.interfaces

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

`ftl/interfaces/IpBlocks.kt`

```kotlin
package ftl.interfaces

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

`ftl/interfaces/NetworkProfile.kt`

```kotlin
package ftl.interfaces

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

`ftl/interfaces/Locales.kt`

```kotlin
package ftl.interfaces

data class Locale(
    val id: String,
    val name: String,
    val region: String,
    val tags: List<String>,
) {
   
    interface Fetch : (projectId: String, platform: String) -> List<Locale>
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

#### Interface

`ftl/interfaces/DeviceModels.kt`

```kotlin
package ftl.interfaces

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

`ftl/interfaces/Orientation.kt`

```kotlin
package ftl.interfaces

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

#### Interface

`ftl/interfaces/OsVersion.kt`

```kotlin
package ftl.interfaces

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
      interface Fetch: (projectId: String) -> List<Android>
   }

   data class Ios(
      val id: String,
      val majorVersion: Int,
      val minorVersion: Int,
      val supportedXcodeVersionIds: List<String>,
      val tags: List<String>,
   ) {
      interface Fetch: (projectId: String) -> List<Ios>
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

### Test matrix results

#### Target

* `SavedMatrix`
* `TestOutcome`
* `TestSuiteOverviewData`

#### Interface

`ftl/interfaces/TestMatrix.kt`

```kotlin
object TestMatrix {
   data class Result(
      val matrixId: String = "",
      val state: String = "",
      val gcsPath: String = "",
      val webLink: String = "",
      val downloaded: Boolean = false,
      val billableVirtualMinutes: Long = 0,
      val billablePhysicalMinutes: Long = 0,
      val clientDetails: Map<String, String>? = null,
      val gcsPathWithoutRootBucket: String = "",
      val gcsRootBucket: String = "",
      val webLinkWithoutExecutionDetails: String? = "",
      val testAxises: List<Outcome> = emptyList()
   )

   data class Outcome(
      val device: String = "",
      val outcome: String = "",
      val details: String = "",
      val testSuiteOverview: SuiteOverview = SuiteOverview()
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
}
```

#### Presentation

```kotlin
TODO()
```

### Execute Android tests matrix

#### Target

* `AndroidRunCommand` -> `AndroidArgs/runAndroidTests` -> `GcAndroidTestMatrix/build`


#### Interface

`ftl/interfaces/AndroidTestMatrix.kt`

```kotlin
package ftl.interfaces

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

      // build
      val otherFiles: Map<String, String>,
      val runGcsPath: String,
      val androidDeviceList: AndroidDeviceList,
      val toolResultsHistory: ToolResultsHistory,
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

   interface Execute: (Config, Type) -> TestMatrix.Result
}
```

#### Presentation

```kotlin
TODO()
```

### Execute Ios tests matrix 

#### Target
* `AndroidRunCommand` -> `AndroidArgs/runAndroidTests` -> `GcAndroidTestMatrix/build`

#### Interface

`ftl/interfaces/IosTestMatrix.kt`

```kotlin
package ftl.interfaces

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

      // build
      val iosDeviceList: IosDeviceList,
      val toolResultsHistory: ToolResultsHistory,
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

   interface Execute: (Config, Type) -> TestMatrix.Result
}
```

#### Presentation

```kotlin
TODO()
```
