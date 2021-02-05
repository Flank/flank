# Flank-scripts command overview

### Command List

- `assemble` - Group of commands to assemble test applications
    - `ios` - Subgroup of commands for iOS app assembly
        - `earl_grey` - Assemble iOS earl grey application
        - `flank_example` - Assemble iOS flank example application
        - `game_loop` - Assemble iOS game loop application
        - `test_plans` - Assemble iOS test plans application
        - `all` - Assemble all iOS applications
    - `android` - Subgroup of command for Android app assembly
        - `app` - Assemble Android test application
    - `go_artifacts` - Generate go artifacts
    
___

- `firebase` - Group of commands for managing firebase integrations
    - `update_api` - Update api schema
    - `generate_client` - Generate Java Client based on api schema
    - `check_for_sdk_updates` - Check for new SDK features and create update tasks for it

___
    
- `github` - Group of command for managing Github   
    - `copy_issue_properties` - Copy properties(assignees, SP, labels) from issue to PR
    - `delete_old_tag` - Delete old/unused tag from Github
    - `delete_release` - Delete release from Github
    - `make_release` - Make new Github release
    
___

- `gradle`   - Group of commands related to gradle tasks (1)
    - `update_dependencies` - Update repository 3rd party dependencies

___

- `integration_tests` - Group of commands for handling integration tests (1)
    - `process_results` - Process results of integration tests
   
___

- `linter` - Group of commands used for applying correct coding style
    - `apply_to_git_hooks` - Apply Linter pre-commit hook
    - `apply_to_ide` - Apply Linter to IDE
  
___

- `release` - Group of commands for creating Flank release
    - `generate_release_notes` - Generate release notes
    - `next_tag` - Get tag for next release
    - `delete_snapshot` - Delete snapshot package from artifacts repository
    - `sync_with_maven_central` - Sync artifacts repository with Maven central

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

  
(1) - currently there is only one command, but probably there will be more in future


### Package structure for CLI

```bash
flank-scripts/
└── cli/
    ├── assemble/
    │   ├── android/
    │   │   └── AppCommand.kt
    │   ├── ios/
    │   │   ├── AllCommand.kt
    │   │   ├── EarlGreyCommand.kt
    │   │   ├── FlankExampleCommand.kt
    │   │   ├── GameLoopCommand.kt
    │   │   └── TestPlansCommand.kt
    │   └── GoArtifactsCommand.kt
    ├── firebase/
    │   ├── CheckForSdkUpdatesCommand.kt
    │   ├── GenerateClientCommand.kt
    │   └── UpdateApiCommand.kt
    ├── github/
    │   ├── DeleteOldTagCommand.kt
    │   ├── DeleteGithubReleaseCommand.kt
    │   ├── MakeGithubReleaseCommand.kt
    │   ├── CopyIssuePropertiesCommand.kt
    ├── gradle/
    │   └── UpdateDependenciesCommand.kt
    ├── integrationtests/
    │   └── ProcessResultsCommand.kt
    ├── linter/
    │   ├── ApplyToGitHooksCommand.kt
    │   └── ApplyToIdeCommand.kt
    ├── release/
    │   ├── DeleteSnapshotCommand.kt
    │   ├── GenerateReleaseNotesCommand.kt
    │   ├── NextTagCommand.kt
    │   └── SyncWithMavenCentralCommand.kt
    └── testartifacts/
        ├── DownloadCommand.kt
        ├── LinkCommand.kt
        ├── PrepareCommand.kt
        ├── RemoveRemoteCommand.kt
        ├── ResolveCommand.kt
        ├── UnzipCommand.kt
        ├── UploadCommand.kt
        └── ZipCommand.kt
```

### Usage
`flankScripts <command group> [<subgroup>] <command name> [<arguments>]`
