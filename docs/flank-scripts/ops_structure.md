### Flank-scripts ops package structure

Flank-scripts `ops` packages mostly match `cli` structure, however, for widely used function there is a separate package
called `common`.
For better code organization `updatebinaries` has a separate package inside `dependencies`, as well as `jfrog` in
`release` package.

Ops package structure and file organization are presented on the tree below:

```bash
├── ops
│   ├── assemble
│   │   ├── BuildFlank.kt
│   │   ├── BuildGo.kt
│   │   ├── android
│   │   │   ├── BuildBaseAndroidApk.kt
│   │   │   ├── BuildBaseAndroidTests.kt
│   │   │   ├── BuildCucumberSampleApk.kt
│   │   │   ├── BuildDuplicatedNamesApks.kt
│   │   │   ├── BuildMultiModulesApks.kt
│   │   │   ├── Common.kt
│   │   │   └── RunAndroidOps.kt
│   │   └── ios
│   │       ├── BuildEarlGreyExample.kt
│   │       ├── BuildExample.kt
│   │       ├── BuildFlankExampleCommand.kt
│   │       ├── BuildFtl.kt
│   │       ├── BuildGameLoopExampleCommand.kt
│   │       ├── BuildIosIPA.kt
│   │       ├── BuildIosTestArtifacts.kt
│   │       ├── BuildTestPlansExample.kt
│   │       ├── IosBuildCommand.kt
│   │       ├── RunFtlLocal.kt
│   │       └── UniversalFramework.kt
│   ├── common
│   │   ├── DownloadSoftware.kt
│   │   ├── EarlGreyExampleConsts.kt
│   │   ├── GenerateChangeLog.kt
│   │   └── ReleaseNotesWithType.kt
│   ├── dependencies
│   │   ├── InstallXcPretty.kt
│   │   ├── SetupIosEnv.kt
│   │   ├── UpdateAllDependencies.kt
│   │   └── common
│   │       ├── DependenciesResultCheck.kt
│   │       ├── DependencyExtensions.kt
│   │       ├── DependencyUpdate.kt
│   │       ├── FindOutdatedDependencies.kt
│   │       ├── GradleDependency.kt
│   │       ├── UpdateDependencies.kt
│   │       ├── UpdateGradle.kt
│   │       ├── UpdatePlugins.kt
│   │       └── UpdateVersionsInFile.kt
│   ├── firebase
│   │   ├── CheckForSDKUpdate.kt
│   │   ├── CommitList.kt
│   │   ├── GenerateJavaClient.kt
│   │   ├── SDKUpdateContext.kt
│   │   ├── UpdateApiJson.kt
│   │   └── common
│   │       └── Extensions.kt
│   ├── github
│   │   ├── CopyGitHubProperties.kt
│   │   ├── DeleteOldRelease.kt
│   │   ├── DeleteOldTag.kt
│   │   ├── DownloadFlank.kt
│   │   └── ReleaseFlank.kt
│   ├── integrationtests
│   │   ├── ProcessIntegrationTestsResult.kt
│   │   └── common
│   │       ├── ITResult.kt
│   │       ├── IntegrationResultContext.kt
│   │       └── PrepareMessage.kt
│   ├── linter
│   │   ├── ApplyKtlintToIdea.kt
│   │   └── LinkGitHooks.kt
│   ├── release
│   │   ├── CreateReleaseNotes.kt
│   │   └── NextReleaseTag.kt
│   ├── testartifacts
│   │   ├── ArtifactsArchive.kt
│   │   ├── Context.kt
│   │   ├── DownloadFixtures.kt
│   │   ├── Helpers.kt
│   │   ├── LinkArtifacts.kt
│   │   ├── PrepareTestArtifacts.kt
│   │   ├── RemoveRemoteCopy.kt
│   │   ├── ResolveArtifacts.kt
│   │   ├── UploadFixtures.kt
│   │   └── ZipArtifacts.kt
│   └── updatebinaries
│       ├── UpdateAtomic.kt
│       ├── UpdateBinaries.kt
│       ├── UpdateLlvm.kt
│       └── UpdateSwift.kt


```
