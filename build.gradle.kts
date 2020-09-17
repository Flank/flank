

// Fix Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.hash.Hashing.crc32c()Lcom/google/common/hash/HashFunction;
// https://stackoverflow.com/a/45286710
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:25.1-jre")
        force(Dependencies.KOTLIN_REFLECT)
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
}

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM) version Versions.KOTLIN
}

repositories {
    jcenter()
    mavenCentral()
}
