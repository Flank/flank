import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    implementation(project(":corellium:api"))
    implementation(project(":corellium:shard:calculate"))
    implementation(project(":corellium:shard:obfuscate"))
    implementation(project(":corellium:shard:dump"))
    implementation(project(":corellium:log"))
    implementation(project(":corellium:junit"))

    testImplementation(Dependencies.JUNIT)
    testImplementation(project(":corellium:adapter"))
}

tasks.test {
    maxHeapSize = "3072m"
    minHeapSize = "512m"
    dependsOn(":resolveArtifacts")
}
