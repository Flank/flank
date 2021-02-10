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
│   │       ├── LipoHelper.kt
│   │       ├── RunFtlLocal.kt
│   │       └── UniversalFramework.kt
│   ├── common
│   │   ├── ConventionalCommitFormatter.kt
│   │   ├── EarlGreyExampleConsts.kt
│   │   ├── GenerateChangeLog.kt
│   │   └── ReleaseNotesWithType.kt
│   ├── dependencies
│   │   ├── DependenciesResultCheck.kt
│   │   ├── DependencyExtensions.kt
│   │   ├── DependencyUpdate.kt
│   │   ├── DownloadSoftware.kt
│   │   ├── FindOutdatedDependencies.kt
│   │   ├── FindVersionInLines.kt
│   │   ├── GradleDependency.kt
│   │   ├── InstallXcPretty.kt
│   │   ├── SetupIosEnv.kt
│   │   ├── UpdateAllDependencies.kt
│   │   ├── UpdateDependencies.kt
│   │   ├── UpdateGradle.kt
│   │   ├── UpdatePlugins.kt
│   │   ├── UpdateVersionsInFile.kt
│   │   └── updatebinaries
│   │       ├── UpdateAtomic.kt
│   │       ├── UpdateBinaries.kt
│   │       ├── UpdateLlvm.kt
│   │       └── UpdateSwift.kt
│   ├── firebase
│   │   ├── CheckForSDKUpdateCommand.kt
│   │   ├── CommitList.kt
│   │   ├── Extensions.kt
│   │   ├── GenerateJavaClient.kt
│   │   ├── LastSDKUpdateRun.kt
│   │   ├── OpenedUpdates.kt
│   │   ├── SDKUpdateContext.kt
│   │   └── UpdateApiJson.kt
│   ├── github
│   │   ├── CopyGitHubProperties.kt
│   │   ├── DeleteOldRelease.kt
│   │   ├── DeleteOldTag.kt
│   │   ├── FindReferenceIssue.kt
│   │   ├── ReleaseFlank.kt
│   │   ├── SetAssignees.kt
│   │   └── SetLabels.kt
│   ├── integrationtests
│   │   ├── CommitList.kt
│   │   ├── Extensions.kt
│   │   ├── IntegrationContext.kt
│   │   ├── IssueList.kt
│   │   ├── PrepareMessage.kt
│   │   ├── ProcessIntegrationTestsResult.kt
│   │   └── WorkflowSummary.kt
│   ├── linter
│   │   ├── ApplyKtlintToIdea.kt
│   │   └── LinkGitHooks.kt
│   ├── release
│   │   ├── AppendReleaseNotes.kt
│   │   ├── CreateReleaseNotes.kt
│   │   ├── NextReleaseTag.kt
│   │   └── jfrog
│   │       ├── DeleteOldSnapshot.kt
│   │       ├── JFrogCommandHelper.kt
│   │       └── SyncMaven.kt
│   └── testartifacts
│       ├── ArtifactsArchive.kt
│       ├── Constants.kt
│       ├── Context.kt
│       ├── DownloadFixtures.kt
│       ├── IsNewVersionAvailable.kt
│       ├── LinkArtifacts.kt
│       ├── PrepareTestArtifacts.kt
│       ├── RemoveRemoteCopy.kt
│       ├── ResolveArtifacts.kt
│       ├── TestArtifactsRepo.kt
│       ├── UploadFixtures.kt
│       └── ZipArtifacts.kt

```
