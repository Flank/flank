import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    implementation(Dependencies.MIXPANEL)
    implementation(Dependencies.JACKSON_KOTLIN)
    implementation(Dependencies.JACKSON_YAML)
    implementation(Dependencies.KOTLIN_REFLECT)

    testImplementation(Dependencies.JUNIT)
}
