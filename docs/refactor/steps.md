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

## Add packages for data layer

1. Add `ftl.interface`
1. Add `ftl.adapter`

## Group external API related code in package

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

#### Output

```kotlin
val printSoftwareCatalogTable: suspend (SoftwareCatalog) -> Unit = TODO()
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

#### Output

```kotlin
val printIpBlocksTable: suspend (List<IpBlock>) -> Unit = TODO()
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

#### Output

```kotlin
val printNetworkProfileDescription: suspend (NetworkProfile) -> Unit = TODO()
val printNetworkProfileList: suspend (List<NetworkProfile>) -> Unit = TODO() 
```

### Locales

#### Target

`ftl/android/AndroidCatalog.kt`

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
   
    interface Fetch : (platform: String, projectId: String) -> List<Locale>
}
```

#### Output

```kotlin
val printLocaleDescription: suspend (Locale) -> Unit = TODO()
val printLocaleTable: suspend (List<Locale>) -> Unit = TODO()
```






### 

#### Target

``

#### Interface

`ftl/interfaces/`

```kotlin
package ftl.interfaces
```

#### Output

```kotlin

```