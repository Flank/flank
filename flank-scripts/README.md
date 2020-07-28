# flank-scripts
This repository contains helper scripts for developing flank. For now, it contains just release related scripts.

## Build and usage

### Build

To build flank-scripts:
1. Run script `buildFlankScripts.sh` in `flank-scripts/bash/` directory
2. Run command `flank-scripts/gradlew clean assemble shadowJar` and manual copy file from `/flank-scripts/build/libs/flankScripts.jar` to `flank-scripts/bash/`
3. You could always run/build it from Intellij IDEA 

### Usage

Run the script with arguments 
`flankScripts COMMAND [ARGS]...`

If you need help with available commands or arguments you could always use option `--help`

## Available commands and options

### Release 
The release process was described in [document](../docs/release_process.md).  
To show all available commands for release use:
`flankScripts release`

Available commands are:
  - `releaseFlank`      Release Flank on GitHub  
  - `updateBugsnag`     Update Bugnsag  
  - `jFrogDelete`       Delete old version on bintray  
  - `jFrogSync`         Sync maven repository using jfrog  
  - `deleteOldRelease`  Delete old release on github  
  - `removeOldTag`      Remove old tag on GitHub  
  
#### `releaseFlank`
Release Flank on GitHub 

Options:  
  `--input-file`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Path to release file  
  `--snapshot `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Is Snapshot release. Default `false`  
  `--git-tag`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git Tag  
  `--commit-hash`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git Commit hash


#### `updateBugsnag`
Update Bugnsag  

Options:  
  `--bugsnag-api-key`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Bugsnag api key  
  `--app-version `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;App version to update  
  `--github-workflow-url`&nbsp;GitHub workflow url 

#### `jFrogDelete`
Delete old version on bintray 

Options:   
`--app-version `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Maven version to delete  

#### `jFrogSync` 
Sync maven repository using jfrog

Options:  
  `--git-tag`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git Tag 

#### `deleteOldRelease` 
Delete old release on github

Options:  
  `--git-tag`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git Tag 


#### `removeOldTag` 
Remove old tag on GitHub

Options:  
  `--git-tag`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git Tag  
  `--username`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git User  
  `--token`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Git Token  
