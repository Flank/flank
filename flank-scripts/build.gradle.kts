import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin(Kotlin.PLUGIN_JVM) version Versions.KOTLIN_VERSION
    kotlin(Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN_VERSION
    id(PLUGIN_SHADOW_JAR) version Versions.SHADOW_JAR
}

val artifactID = "flankScripts"

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    archiveClassifier.set("")
    archiveBaseName.set(artifactID)
    mergeServiceFiles()

    @Suppress("UnstableApiUsage")
    manifest {
        attributes(mapOf("Main-Class" to "flank.scripts.MainKt"))
    }
}

version = "1.0"
group = "flank.scripts"

application {
    mainClassName = "flank.scripts.MainKt"
}


repositories {
    jcenter()
    mavenCentral()
    maven(url =  "https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(KOTLIN_SERIALIZATION)
    implementation(Fuel.CORE)
    implementation(Fuel.KOTLINX_SERIALIZATION)
    implementation(Fuel.COROUTINES)
    implementation(CLIKT)
}

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "flank.scripts.MainKt"
    }
}
