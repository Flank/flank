# Test artifacts
Test artifacts are necessary for CI and local testing.

### Generating test artifacts
```bash
cd ... # your flank repositry root
source .env
update_test_artifacts android ios  # [ android | go | ios | all ]
```
`update_test_artifacts` function will generate artifacts and copy them to `test_runner/src/test/kotlin/ftl/fixtures/tmp/` directory.

# Refactor
This is proposal of test artifacts refactoring.

## Context 
Working on different issues sometimes requires different implementations of test artifacts that may exist in same time.

### Problem
- Updating test artifacts is not convenient as could be. Currently, it requires few manual steps:
```
1. Generate new artifacts
2. Download zip with lastest artifacts from https://github.com/Flank/test_artifacts/releases/tag/latest
3. Upack lastest artifacts
4. Copy new artifacts to proper directory
5. Create proper zip with updated artifacts (on mac it may be tricky) 
6. Upload new bundle using https://github.com/Flank/test_artifacts
```
- Current test artifacts design doesn't fit well with asynchronous work on multiple branches, 
because we are sharing single bundle of artifacts between CI on remote branches and local working copies.
- Test artifacts download mechanism is embedded in mock-server initialization and typically performed before tests run. 
Sometimes downloading may fail, but this not aborting test run.
- Source code of artifacts is scattered between repositories.
- Scripts for building artifacts are not complete.


### Requirements
1. Source code for test artifacts should be kept on the single repository for easier manage. 
2. Generating new artifacts and coping to proper directory for local work purpose should be easy as single command run.
3. CI should be able to use dedicated test artifacts for each branch.
4. Updating remote copy of test artifacts should be easy as possible.

### Technical research
For remote storage options see [host_binaries_solutions_comparison.md](./host_binaries_solutions_comparison.md)

## Solution proposal
1. ~~Move source code of test artifacts to one repo. 
We should consider flank repo because test_app is already there or dedicated repo linked to flank as git submodule.~~
2. ~~Prepare missing build scripts and adjust existing.~~
We need dedicated scripts for each artifact group (iOS, Android or different types of each) and one to execute all of them.
3. Choose the best solution for hosting remote artifacts. See `Technical research` section.
4. Integrate artifacts synchronization with remote storage
