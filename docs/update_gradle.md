# Gradle

## Updating Gradle
> brew upgrade gradle

> gradle --version

> gradle wrapper --distribution-type all

Specifiy the gradle distribution type in `build.gradle`:
```
wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
```

