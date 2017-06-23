## Gradle

When creating a new project, setup symlinks to gradle. This allows all projects to
use the same Gradle config.

```bash
ln -sf ../gradle/gradle ./gradle
ln -sf ../gradle/gradle.properties ./gradle.properties
ln -sf ../gradle/gradlew ./gradlew
ln -sf ../gradle/gradlew.bat ./gradlew.bat
```
