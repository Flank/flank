# Flank-scripts command overview

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
  - `save_service_account` - Save given service account to flank credentials location

___

- `github` - Group of command for managing GitHub integration
    - `copy_issue_properties` - Copy properties(assignees, story points, labels) from issue to pull request
    - `delete_old_tag` - Delete old tag on GitHub
    - `delete_release` - Delete old release on github
    - `make_release` - Make new GitHub release
    - `download_flank` - Downloads flank.jar with selected version.

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

### Usage

`flankScripts <command group> [<subgroup>] <command name> [<arguments>]`
