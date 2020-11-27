# Release process

## Requirements

1. A release process should be run withing macOS environment
2. The machine should contain:
    `homebrew` - Package manager
    `hub` - Github CLI tool

## Current setup

Current scripts run on GitHub actions environment with `macos-latest` os.
Script could be found on [path](../.github/workflows/release.yml)

Each push:
- to `master` branch run Snapshot release
- of tag `v*` run regular release

## Triggering release 

### Manually
1. Navigate to [Github Actions](https://github.com/Flank/flank/actions)
2. Run job [`Generate release notes for next commit`](https://github.com/Flank/flank/actions?query=workflow%3A%22Generate+release+notes+for+next+commit%22) by using `Run Workflow` button
3. After merging PR,  the next tag will be pushed to repository
4. Wait for CI job to finish

### Automatically
1. Release job will run each 1st day of month
2. After merging PR,  the next tag will be pushed to repository
2. Wait for CI job to finish

## CI Steps
1. Gradle Build flankScripts and add it to PATH
2. Set environment variables
3. Update bugsnag
4. Get Jfrog CLI
5. Delete old snapshot
6. Gradle Build Flank
7. Gradle Upload to bintray
8. Authenticate to hub
9. Remove old release
10. Rename old tag  
11. Release flank
    1. Snapshot for snapshot flow (push to master)
    2. Stable for regular flow (push tag `v*`)
12. Sync bintray to maven central
