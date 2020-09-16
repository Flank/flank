# flank-scripts
This repository contains helper scripts for developing flank. For now, it contains just release related scripts.

## Build and usage

### Build

To build flank-scripts:
1. Run script `buildFlankScripts.sh` in `flank-scripts/bash/` directory
2. Run command `./gradlew clean flank-scripts:assemble flank-scripts:shadowJar` and manual copy file from `/flank-scripts/build/libs/flankScripts.jar` to `flank-scripts/bash/`
3. You could always run/build it from Intellij IDEA 

### Usage

Run the script with arguments 
`flankScripts COMMAND [ARGS]...`

If you need help with available commands or arguments you could always use option `--help`

## Available commands and options

### CI
To show all available commands for ci use:
`flankScripts ci`

Available commands are:
  - `generateReleaseNotes`  Command to generate release notes and append them to `release_notes.md`
  - `nextReleaseTag`        Print next release tag
  
#### `generateReleaseNotes`
Command to generate release notes and append them to `release_notes.md`

| Option               	| Description                                             	|
|----------------------	|---------------------------------------------------------	|
| --token              	| Git token                                               	|
| --release-notes-file 	| Path to release_notes.md (default `./release_notes.md`) 	|

#### `nextReleaseTag`
Print next release tag

| Option          	| Description     	|
|-----------------	|-----------------	|
| `--token`       	| Git token       	|

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
  - `deleteOldTag`      Delete old tag on GitHub  
  
#### `releaseFlank`
Release Flank on GitHub 

| Option          	| Description                          	|
|-----------------	|--------------------------------------	|
| `--input-file`  	| Path to release file                 	|
| `--snapshot `   	| Is Snapshot release. Default `false` 	|
| `--git-tag`     	| Git Tag                              	|
| `--commit-hash` 	| Git Commit hash                      	|
|  `--token`        | Git token                             |


#### `updateBugsnag`
Update Bugnsag  

| Option                  	| Description                   	|
|-------------------------	|-------------------------------	|
| `--bugsnag-api-key`     	| Bugsnag api key               	|
| `--app-version `        	| App version to update         	|
| `--github-workflow-url` 	| GitHub workflow url. Optional 	|

#### `jFrogDelete`
Delete old version on bintray 
 
| Option          	| Description                          	|
|-----------------	|--------------------------------------	|
| --version       	| Maven version ( without leading `v`) 	|

#### `jFrogSync` 
Sync maven repository using jfrog

| Option          	| Description                          	|
|-----------------	|--------------------------------------	|
| `--git-tag`     	| Git Tag                              	|

#### `deleteOldRelease` 
Delete old release on github

| Option          	| Description                          	|
|-----------------	|--------------------------------------	|
| `--git-tag`     	| Git Tag                              	| 


#### `deleteOldTag` 
Delete old tag on GitHub

| Option          	| Description     	|
|-----------------	|-----------------	|
| --git-tag`      	| Git tag         	|
| `--username`    	| Git username    	|
| `--token`       	| Git token       	|
