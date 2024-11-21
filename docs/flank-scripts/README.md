# flank-scripts
This repository contains helper scripts for developing flank. For now, it contains just release related scripts.

## Build and usage

### Build

To build flank-scripts:
  - Run script `buildFlankScripts.sh` in `flank-scripts/bash/` directory
  - Run command `./gradlew clean flank-scripts:assemble flank-scripts:shadowJar` and manual copy file from `/flank-scripts/build/libs/flank-scripts.jar` to `flank-scripts/bash/`
  - You could always run/build it from IntelliJ IDEA

### Usage

Run the script with arguments
`flankScripts <command group> [<subgroup>] <command name> [<arguments>]`

If you need help with available commands or arguments you could always use the option `--help`

## Available commands and options

### Command List

- `assemble` - Group of commands to assemble application
  - `android` - Subgroup of commands for Android test application assembly
    - `app` - Assemble Android test application
  - `ios` - Subgroup of commands for iOS test applications assembly
    - `earl_grey` - Assemble iOS earl grey application
    - `example` - Assemble iOS example application
    - `flank_example` - Assemble iOS flank example application
    - `ftl` - Assemble iOS ftl example application
    - `game_loop` - Assemble iOS game loop application
    - `test_plans` - Assemble iOS test plans application
    - `all` - Assemble all iOS applications
  - `flank` - Build Flank
  - `go_artifacts` - Generate go artifacts

___

- `dependencies`   - Group of commands related to dependencies tasks
  - `install_xcpretty` - Install xcpretty formatter
  - `setup_ios_env` - Setup iOS environment
  - `universal_framework_files` - Create Universal Framework files
  - `update_binaries` - Update binaries used by Flank
  - `update` - Update repository 3rd party dependencies

___

- `firebase` - Group of commands for managing firebase integrations
  - `check_for_sdk_updates` - Check for new SDK features and create update tasks for it
    - `generate_client` - Generate Java Client based on api schema
    - `update_api` - Update api schema

___

- `github` - Group of command for managing GitHub integration
  - `copy_issue_properties` - Copy properties(assignees, story points, labels) from issue to pull request
  - `delete_old_tag` - Delete old tag on GitHub
  - `delete_release` - Delete old release on github
  - `make_release` - Make new GitHub release

___

- `integration_tests` - Group of commands for handling integration tests (1)
  - `process_results` - Process results of integration tests

___

- `linter` - Group of commands used for applying correct coding style
  - `apply_to_git_hooks` - Apply Linter pre-commit hook
  - `apply_to_ide` - Apply Linter to IDE

___

- `release` - Group of commands for creating Flank release
  - `delete_snapshot` - Delete snapshot package from artifacts repository
  - `generate_release_notes` - Generate release notes
  - `next_tag` - Get tag for next release
  - `sync_with_maven_central` - Sync artifact's repository with Maven central

___

- `test_artifacts` - Group of commands for artifacts management
  - `download` - Download test artifacts zip asset to test_artifacts directory.
  - `link` - Create symbolic link to under test_runner/src/test/kotlin/ftl/fixtures/tmp to
    test_artifacts/{branchName}.
  - `prepare`- Creates a fresh copy of test artifacts for the current working branch, basing on an existing one.
  - `remove_remote` - Remove remote copy of test artifacts.
  - `resolve` - Automatically prepare local artifacts if needed.
  - `unzip` - Unpack test artifacts zip archive.
  - `upload` - Upload test artifacts zip as github release asset.
  - `zip` - Create zip archive from test artifacts directory.

(1) - please note that there is only one command, but it may change in the future.


### Arguments

To show applicable arguments for command use `--help` or `-h` options:
`flankScripts <command group> [<subgroup>] <command name> --help`
or
`flankScripts <command group> [<subgroup>] <command name> -h`

## Testing
To test your script with different settings use the `flank-debug.properties` file. Uncomment and replace with desired values.
Properties are skipped by git and should not be attached to a commit. Note, the `test` task ignores your own properties and will use the default.

#### List of possible configs
| Key         | Description         | Default value |
|:----------------|:---------------------|:---:|
|`repo.flank`|Flank test runner repo. Essential property for github client.|`Flank/flank`|
|`repo.gcloud_cli`|Flank's fork of gcloud sdk repo.|`Flank/gcloud_cli`|
|`repo.test-artifacts`|Flank's source of artifacts (apks, binaries, etc) used for testing|`Flank/test_artifacts`|
|`integration.workflow-filename`|GH Action integration tests workflow file. Used to fetch list of commits since it's last run.|`full_suite_integration_tests.yml`|
|`integration.issue-poster`|Name of account that creates IT issue|`github-actions[bot]`|
|`sdk-check.workflow-filename`|GH Action dependencies update workflow file. Used to fetch list of commits since it's last run.|`update_dependencies_and_client.yml`|
|`sdk-check.issue-poster`|Name of account that creates dependencies issues/epics|`github-actions[bot]`|

## Directory structure

```bash
.
├── cli
│   ├── Main.kt
│   ├── assemble
│   │   ├── AssembleCommand.kt
│   │   ├── FlankCommand.kt
│   │   ├── GoCommand.kt
│   │   ├── android
│   │   │   ├── AndroidCommand.kt
│   │   │   └── AppCommand.kt
│   │   └── ios
│   │       ├── EarlGreyCommand.kt
│   │       ├── ExampleCommand.kt
│   │       ├── FlankExampleCommand.kt
│   │       ├── FtlCommand.kt
│   │       ├── GameLoopExampleCommand.kt
│   │       ├── IosCommand.kt
│   │       ├── RunFtlLocalCommand.kt
│   │       └── TestPlansExample.kt
│   ├── dependencies
│   │   ├── DependenciesCommand.kt
│   │   ├── InstallXcPrettyCommand.kt
│   │   ├── SetupIosEnvCommand.kt
│   │   ├── UniversalFrameworkCommand.kt
│   │   ├── UpdateBinariesCommand.kt
│   │   └── UpdateCommand.kt
│   ├── firebase
│   │   ├── CheckForSdkUpdatesCommand.kt
│   │   ├── FirebaseCommand.kt
│   │   ├── GenerateClientCommand.kt
│   │   └── UpdateApiCommand.kt
│   ├── github
│   │   ├── CopyIssuePropertiesCommand.kt
│   │   ├── DeleteOldTagCommand.kt
│   │   ├── DeleteReleaseCommand.kt
│   │   ├── DownloadFlankCommand.kt
│   │   ├── GitHubCommand.kt
│   │   └── MakeReleaseCommand.kt
│   ├── integrationtests
│   │   ├── IntegrationTestsCommand.kt
│   │   └── ProcessResultCommand.kt
│   ├── linter
│   │   ├── ApplyToGitHooksCommand.kt
│   │   ├── ApplyToIdeCommand.kt
│   │   └── LinterCommand.kt
│   ├── release
│   │   ├── GenerateReleaseNotesCommand.kt
│   │   ├── NextTagCommand.kt
│   │   └── ReleaseCommand.kt
│   └── testartifacts
│       ├── DownloadCommand.kt
│       ├── LinkCommand.kt
│       ├── PrepareCommand.kt
│       ├── RemoveRemoteCommand.kt
│       ├── ResolveCommand.kt
│       ├── TestArtifactsCommand.kt
│       ├── UnzipCommand.kt
│       ├── UploadCommand.kt
│       └── ZipCommand.kt
├── data
│   └── github
│       ├── GitHubErrorResponse.kt
│       ├── GithubApi.kt
│       ├── commons
│       │   └── LastWorkflowRunDate.kt
│       └── objects
│           ├── GitHubCommit.kt
│           ├── GitHubCreateIssue.kt
│           ├── GitHubCreateIssueComment.kt
│           ├── GitHubRelease.kt
│           ├── GitHubSetAssigneesRequest.kt
│           ├── GitHubSetLabelsRequest.kt
│           ├── GitHubUpdateIssue.kt
│           ├── GitHubWorkflowRun.kt
│           └── GithubPullRequest.kt
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
└── utils
    ├── Env.kt
    ├── FastFailForWindows.kt
    ├── Git.kt
    ├── GradleCommand.kt
    ├── MarkdownFormatter.kt
    ├── Path.kt
    ├── Serialization.kt
    ├── ShellExecute.kt
    ├── Version.kt
    └── exceptions
        ├── FlankScriptsExceptionMappers.kt
        └── FlankScriptsExceptions.kt


```
