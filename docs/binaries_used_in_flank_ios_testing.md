# Binaries used in Flank's iOS testing

## Location

Binaries are placed in [Flank binaries repository](https://github.com/Flank/binaries)

## Usage

There is gradle task which download binaries and copy them to resources. 
This task runs only on Linux and Windows, because on MacOS the binaries are part of system files.
If you would like to run script manually use Gradle command:  
```
gradlew :test_runner:downloadBinaries
```
You could also configure force update by using `forceUpdate` option

`test_runner/build.gradle.kts`
```kotlin
if (!DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
    val downloadBinaries by tasks.registering(DownloadBinariesTask::class) {
        forceUpdate = true
    }
    tasks.compileKotlin { dependsOn(downloadBinaries) }
}
```

## Updating

In order to update binaries just follow below steps:
1. checkout binaries [repository](https://github.com/Flank/binaries)
1. update them using:
   - `updateBinariesWithFlankBash` will update binaries for Linux and Windows using `flank-scripts`
   - `update.sh` (old method). It will update binaries for Linux OS
1. commit and push files (create PR with changes)
1. once they will be on master branch. CI job will update artifacts with proper files based on OS
