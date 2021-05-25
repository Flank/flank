repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins.register("maven-version-check") {
        id = "maven-version-check"
        implementationClass = "com.github.flank.gradle.MavenVersionCheck"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2") // unfortunately it does not work with buildSrc Dependencies
}

