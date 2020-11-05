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
    applicationDefaultJvmArgs = listOf(
        "-Xmx2048m",
        "-Xms512m"
    )
}

tasks.test {
    maxHeapSize = "2048m"
    minHeapSize = "512m"
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
    implementation(Dependencies.JSOUP)
    implementation(Dependencies.JCABI_GITHUB)
    implementation(Dependencies.SLF4J_NOP)
    implementation(Dependencies.GLASSFISH_JSON)
    implementation(Dependencies.ARCHIVE_LIB)
    implementation(Dependencies.TUKAANI_XZ)

    detektPlugins(Dependencies.DETEKT_FORMATTING)

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TRUTH)
    testImplementation(Dependencies.SYSTEM_RULES)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

val prepareJar by tasks.registering(Copy::class) {
    dependsOn("shadowJar")
    from("$buildDir/libs")
    include("flankScripts.jar")
    into("$projectDir/bash")
}
