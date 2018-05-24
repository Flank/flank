## CLA

A CLA is required to contribute to flank. See [walmart labs](https://github.com/walmartlabs/walmart-cla#walmart-contributor-license-agreement-cla) for more information. Google's open source policy [explains why CLAs are commonly used](https://opensource.google.com/docs/cla/policy/)

## Gradle

When creating a new project, setup symlinks to gradle. This allows all projects to
use the same Gradle config.

```bash
ln -sf ../gradle/gradle
ln -sf ../gradle/gradle.properties
ln -sf ../gradle/gradlew
ln -sf ../gradle/gradlew.bat
```
