import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
    id(Plugins.DETEKT_PLUGIN)
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
    parallel = true
    autoCorrect = true

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
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
    implementation(Dependencies.KOTLIN_SERIALIZATION)
    implementation(Dependencies.Fuel.CORE)
    implementation(Dependencies.Fuel.KOTLINX_SERIALIZATION)
    implementation(Dependencies.Fuel.COROUTINES)
    implementation(Dependencies.CLIKT)

    detektPlugins(Dependencies.DETEKT_FORMATTING)

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TRUTH)
    testImplementation(Dependencies.SYSTEM_RULES)
}
