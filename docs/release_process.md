# Release process

## Requirements

1. A release process should be run withing macOS environment
2. The machine should contain:
    `homebrew` - Package manager
    `gh` - GitHub CLI tool

## Current setup

Current scripts run on GitHub actions environment with `macos-latest` os.
Script could be found on [path](../.github/workflows/release.yml)

Each push:
- to `master` branch run Snapshot release
- of tag `v*` run regular release

## Triggering release 

### Manually
1. Navigate to [GitHub Actions](https://github.com/Flank/flank/actions)
2. Run job [`Generate release notes for next commit`](https://github.com/Flank/flank/actions?query=workflow%3A%22Generate+release+notes+for+next+commit%22) by using `Run Workflow` button
3. After merging PR,  the next tag will be pushed to repository
4. Wait for CI job to finish

### Automatically
1. Release job will run each 1st day of month
2. After merging PR,  the next tag will be pushed to repository
2. Wait for CI job to finish

## CI Steps
1. Gradle Build flankScripts and add it to PATH
1. Set environment variables
1. Delete old snapshot
1. Gradle Build Flank
1. Authenticate to hub
1. Remove old release
1. Rename old tag  
1. Release flank
    1. Snapshot for snapshot flow (push to master)
    2. Stable for regular flow (push tag `v*`)
1. Publish binary for Maven Central
1. Publish binary to GithubPackages (non snapshot only)
