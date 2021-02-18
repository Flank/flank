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

## Group external API related code in package

TODO