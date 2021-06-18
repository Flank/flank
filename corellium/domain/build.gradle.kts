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
    implementation(project(":tool:apk"))
    implementation(project(":tool:shard:calculate"))
    implementation(project(":tool:shard:obfuscate"))
    implementation(project(":tool:shard:dump"))
    implementation(project(":tool:instrument:command"))
    implementation(project(":tool:instrument:log"))
    implementation(project(":tool:junit"))

    testImplementation(Dependencies.JUNIT)
    testImplementation(project(":corellium:adapter"))
}

tasks.test {
    maxHeapSize = "3072m"
    minHeapSize = "512m"
    dependsOn(":resolveArtifacts")
}
