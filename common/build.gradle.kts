import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Paths

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
}

tasks.test {
    maxHeapSize = "2048m"
    minHeapSize = "512m"
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
    // Fuel
    api(Dependencies.Fuel.CORE)
    api(Dependencies.Fuel.KOTLINX_SERIALIZATION)
    api(Dependencies.Fuel.COROUTINES)
    // Archive
    api(Dependencies.ARCHIVE_LIB)
    api(Dependencies.TUKAANI_XZ)

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TRUTH)

    testImplementation(Dependencies.SYSTEM_RULES)
}
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

file("flank-debug.properties").run {
    if (!exists() && System.getenv("CI") != null) writeText(
        """
            #zenhub.repo-id=84221974
            #
            #repo.flank=Flank/flank
            #repo.gcloud_cli=Flank/gcloud_cli
            #repo.test-artifacts=Flank/test_artifacts
            #
            #integration.workflow-filename=full_suite_integration_tests.yml
            #integration.issue-poster=github-actions[bot]
            #
            #sdk-check.workflow-filename=update_dependencies_and_client.yml
            #sdk-check.issue-poster=github-actions[bot]
        """.trimIndent()
    )
}
