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
    implementation(Dependencies.DEX_TEST_PARSER)
    implementation(Dependencies.APK_PARSER)

    testImplementation(Dependencies.JUNIT)
}

tasks.test {
    maxHeapSize = "3072m"
    minHeapSize = "512m"
}
