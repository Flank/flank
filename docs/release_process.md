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

## Manual Steps
1. Add the version number to [release notes](https://github.com/Flank/flank/blob/master/release_notes.md)
2. Add the version number to [version numbers](https://github.com/Flank/flank/blob/master/contributing.md#version-numbers)
3. Make a commit with the updated release notes and create PR for them.
4. After merging, push a tag for the release
5. Wait for CI job to finish
6. Go to releases section on GitHub and add the release notes from the release_notes.md file

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
