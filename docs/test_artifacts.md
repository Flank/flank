# Test artifacts
Test artifacts are precompiled binaries necessary for CI and local testing.
They can change over time so developers should be able to maintain and share them easy as possible.
 
## Overview

### Storage

#### Local
Local working copy of test artifacts can be find in [test_artifacts](../test_artifacts) directory inside local flank repository. 
However, this directory MUST be added to .gitignore to prevent committing binaries to git.
We accept few restrictions about storing test artifacts in [test_artifacts](../test_artifacts) directory.
* [test_artifacts](../test_artifacts) directory CAN contain directories with binaries required for testing.
* Names of directories inside [test_artifacts](../test_artifacts) SHOULD reflect names of working branches.
* [test_artifacts](../test_artifacts) directory CAN contain test artifacts archives bundles.
* The name of artifact archive should match following format `branchName-unixTimestamp.zip`

#### Remote
The remote copy of test artifacts can be find at [test_artifacts/releases](https://github.com/Flank/test_artifacts/releases)
We accept few restrictions about using github releases as storage for test artifacts.
* The release name and tag should reflect the name of related working branch.
* Each release should contain only one asset with artifacts.
* The name of test artifact assets archive should be [ ~~mdp5 checksum~~ | unix timestamp ] suffixed with `zip` extensions. 

### Linking artifacts
For convenient switching between test artifacts we are using symbolic links.
Ensure you have symbolic [link](../test_runner/src/test/kotlin/ftl/fixtures/tmp) to correct directory inside [test_artifacts](../test_artifacts) directory,
otherwise the unit tests will fail because of lack of binaries.

### Test projects
All source code of test artifacts binaries can be find in [test_projects](../test_projects) directory.

### Generating test artifacts
```bash
source .env
update_test_artifacts android ios  # [ android | go | ios | all ]
```

### Working with artifacts
All [testArtifacts](../flank-scripts/src/main/kotlin/flank/scripts/testartifacts/TestArtifacts.kt) subcommands can be configured using base options.
* `testArtifacts -b {name}` used to specify branch name for test artifacts. 
For example if you want to run any subcommand on artifacts dedicated for your working branch `feature123` run `testArtifacts -b feature123 {subcommand}`.
By default, it is the name of current working git branch.

* `testArtifacts -p {path}` used to specify the path to local flank repository. 
By default, the path is read from `FLANK_ROOT`env variable. 
To export path to your local flank repository just source [.env](../.env) file. 

## Developer scenarios

> As a developer I want to download test artifacts before test run.

* Just run `./gradlew test` command, this should trigger `checkArtifacts` task which will update artifacts if needed.

> As a developer I want to switch between local test artifacts

* Run `flankScripts testArtifacts link` to create a symbolic link to test artifacts for the current branch.
* Run `flankScripts testArtifacts -b {name} link` to create a symbolic link to test artifacts for the specific branch.

> As a developer I want to edit existing test artifacts

1. Edit required project in [test_artifacts/releases](https://github.com/Flank/test_artifacts/releases) directory.
1. Ensure you have sourced [.env](../.env) file.
1. Build required project and copy binaries using [update_test_artifacts](../test_projects/ops.sh) shell function or do it manually.
1. Ensure you have linked a correct directory with artifacts.
1. Your local tests now should you use updated artifacts.  

> As a developer I want to upload new test artifacts to remote repository.

1. Make sure you have directory with artifacts in [test_artifacts](../test_artifacts) and the name same as working branch.
1. Run `flankScripts testArtifacts zip` to create zip archive.
1. Run `flankScripts testArtifacts upload` to upload zip as remote copy. The script will automatically resolve the latest archive and upload it.

> As a developer I want to remove test artifacts

* Run `flankScripts testArtifacts remove` this will remove the remote copy of test artifacts for current working branch.
