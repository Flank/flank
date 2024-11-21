# Dependencies update process

## Description
Process run commands and update files with defined versions in the provided file and create PR with changes.

### Modules
0. [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) which check dependencies version and generate report
0. [Command](../flank-scripts/README.md#dependencies) in `flank-scripts` which update dependencies versions
0. GitHub action job which runs dependencies check every Monday at 5 AM UTC or on-demand

## Usage

### Manually (root directory)
0. Generate report using command `./gradlew dependencyUpdates -DoutputFormatter=json -DoutputDir=.`
0. Build flank scripts using script `./flank-scripts/bash/buildFlankScripts.sh`
0. Run `./flank-scripts/bash/flankScripts dependencies update`

### GitHub action
Run `Update dependencies` job using [GitHub action menu](https://github.com/Flank/flank/actions) by clicking `Run workflow` button

## Merging to master

### Success path
If all PR jobs will succeed it means that dependencies update will not break the current code base and pull requests could be successfully merged.

### Failure path
If any of PR job will fail, it means that dependencies update will break our codebase and code should be aligned before merging
