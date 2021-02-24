import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
}

application {
    mainClassName = "flank.corellium.sandbox.ExampleCorelliumRun"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
    implementation(project(":corellium:corellium-client"))
}

val runExampleScript by tasks.registering(JavaExec::class) {
    dependsOn(tasks.build)
    classpath = sourceSets["main"].runtimeClasspath
    main = "flank.corellium.sandbox.ExampleCorelliumRun"
}

file("./src/main/resources/corellium-config.properties").also { propFile ->
    if (!propFile.exists() && System.getenv("CI") == null)
        file("./src/main/resources/corellium-config.properties_template").copyTo(propFile)
}
