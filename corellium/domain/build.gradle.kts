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
    api(project(":corellium:api"))
    api(project(":tool:execution:linear"))
    api(project(":tool:apk"))
    api(project(":tool:filter"))
    api(project(":tool:shard:calculate"))
    api(project(":tool:shard:obfuscate"))
    api(project(":tool:shard:dump"))
    api(project(":tool:instrument:command"))
    api(project(":tool:instrument:log"))
    api(project(":tool:junit"))
    api(project(":tool:log"))
    api(project(":tool:analytics"))
    api(project(":tool:analytics:mixpanel"))

    testImplementation(Dependencies.JUNIT)
    testImplementation(project(":corellium:adapter"))
}

tasks.test {
    maxHeapSize = "3072m"
    minHeapSize = "512m"
    dependsOn(":resolveArtifacts")
}
