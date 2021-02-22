import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

application {
    mainClassName = "flank.corellium.sandbox.CorelliumRun"
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
    implementation(project(":corellium:corellium-client"))
}
