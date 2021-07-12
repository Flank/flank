import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    api(Dependencies.JACKSON_KOTLIN)
    api(Dependencies.JACKSON_YAML)
    api(Dependencies.JACKSON_XML)
    testImplementation(Dependencies.JUNIT)
}
