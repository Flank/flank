repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins.register("check-version-updated") {
        id = "check-version-updated"
        implementationClass = "com.github.flank.gradle.CheckVersionUpdated"
    }
}
