import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin(Kotlin.PLUGIN_JVM) version Versions.KOTLIN_VERSION
    kotlin(Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN_VERSION
    id(Libs.PLUGIN_SHADOW_JAR) version Versions.SHADOW_JAR
    id(Libs.DETEKT_PLUGIN) version Versions.DETEKT
}

val artifactID = "flankScripts"

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    archiveClassifier.set("")
    archiveFileName.set("$artifactID.jar")
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
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("../config/detekt.yml")
    reports {
        xml {
            enabled = false
        }
        html {
            enabled = true
        }
    }
}

tasks["check"].dependsOn(tasks["detekt"])

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Libs.KOTLIN_SERIALIZATION)
    implementation(Libs.Fuel.CORE)
    implementation(Libs.Fuel.KOTLINX_SERIALIZATION)
    implementation(Libs.Fuel.COROUTINES)
    implementation(Libs.CLIKT)

    detektPlugins(Libs.DETEKT_FORMATTING)

    testImplementation(Libs.JUNIT)
    testImplementation(Libs.MOCKK)
    testImplementation(Libs.TRUTH)
    testImplementation(Libs.SYSTEM_RULES)
}
