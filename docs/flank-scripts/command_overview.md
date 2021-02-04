# Flank-scripts command overview

### Command List

- `release` - Group of commands for creating Flank release
    - `generate_release_notes` - Generate release notes
    - `next_tag` - Get tag for next release
    - `delete_github_release` - Delete release from Github
    - `delete_snapshot` - Delete snapshot package from artifacts repository
    - `delete_old_tag` - Delete old/unused tag from Github
    - `sync_with_maven_central` - Sync artifacts repository with Maven central
    - `make_github_release` - Make new Github release

___
    
- `coding_style` - Group of commands used for applying correct coding style
    - `apply_linter_to_git_hooks` - Apply KtLint pre-commit hook
    - `apply_linter_to_ide` - Apply KtLint to IDE
  
___
    
- `update_dependencies` - Update repository dependencies

___

- `integration_tests` - Group of commands for handling integration tests (1)
    - `process_results` - Process results of integration tests


(1) - currently there is only one command, but probably there will be more in future
___
    
- `copy_issue_properties` - Copy properties(assignees, SP, labels) from issue to PR

___

- `test_artifacts` - Group of commands for artifacts management
    - `download` - Download test artifacts zip asset to test_artifacts directory.
    - `link` - Create symbolic link to under test_runner/src/test/kotlin/ftl/fixtures/tmp to test_artifacts/{branchName}.
    - `prepare`- Creates fresh copy of test artifacts for current working branch, basing on existing one.
    - `remove_remote` - Remove remote copy of test artifacts.
    - `resolve` - Automatically prepare local artifacts if needed.
    - `unzip` - Unpack test artifacts zip archive.
    - `upload` - Upload test artifacts zip as github release asset.
    - `zip` - Create zip archive from test artifacts directory.

___

- `firebase` - Group of commands for managing firebase integrations
    - `update_api` - Update api schema
    - `generate_client` - Generate Java Client based on api schema
    - `check_for_sdk_updates` - Check for new SDK features and create tasks for it

___
    
- `assemble` - Group of commands for assemble test application
    - `android_app` - Assemble Android test application
    - `go_artifacts` - Generate go artifacts
    - `ios_earl_grey` - Assemble iOS earl grey application
    - `ios_flank_example` - Assemble iOS flank example application
    - `ios_game_loop` - Assemble iOS game loop application
    - `ios_test_plans` - Assemble iOS test plans application
    - `ios_all` - Assemble all iOS applications
  

### Package structure for CLI

```bash
flank-scripts/
└── cli/
    ├── assemble/
    │   ├── AndroidAppCommand.kt
    │   ├── GoArtifactsCommand.kt
    │   ├── IosAllCommand.kt
    │   ├── IosEarlGreyCommand.kt
    │   ├── IosFlankExampleCommand.kt
    │   ├── IosGameLoopCommand.kt
    │   └── IosTestPlansCommand.kt
    ├── codingstyle/
    │   ├── ApplyLinterToGitHooksCommand.kt
    │   └── ApplyLinterToIdeCommand.kt
    ├── firebase/
    │   ├── CheckForSdkUpdatesCommand.kt
    │   ├── GenerateClientCommand.kt
    │   └── UpdateApiCommand.kt
    ├── integrationtests/
    │   └── ProcessResultsCommand.kt
    ├── release/
    │   ├── DeleteSnapshotCommand.kt
    │   ├── DeleteOldTagCommand.kt
    │   ├── DeleteGithubReleaseCommand.kt
    │   ├── GenerateReleaseNotesCommand.kt
    │   ├── MakeGithubReleaseCommand.kt
    │   ├── NextTagCommand.kt
    │   └── SyncWithMavenCentralCommand.kt
    ├── testartifacts/
    │   ├── DownloadCommand.kt
    │   ├── LinkCommand.kt
    │   ├── PrepareCommand.kt
    │   ├── RemoveRemoteCommand.kt
    │   ├── ResolveCommand.kt
    │   ├── UnzipCommand.kt
    │   ├── UploadCommand.kt
    │   └── ZipCommand.kt
    ├── UpdateDependenciesCommand.kt
    └── CopyIssuePropertiesCommand.kt
```

### Usage
`flankScripts <command group> <command name> <arguments>`
