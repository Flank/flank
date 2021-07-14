import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    api(Dependencies.Fuel.CORE)
    api(Dependencies.ARCHIVE_LIB)
    api(Dependencies.JSON)

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
    if (!exists() && System.getenv("CI") == null) writeText(
        """
            #zenhub.repo-id=84221974
            #
            #repo.flank=Flank/flank
            #repo.gcloud_cli=Flank/gcloud_cli
            #repo.test-artifacts=Flank/test_artifacts
            #
            #integration.workflow-filename=integration_tests_pointer.yml
            #integration.issue-poster=github-actions[bot]
            #
            #sdk-check.workflow-filename=update_dependencies_pointer.yml
            #sdk-check.issue-poster=github-actions[bot]
        """.trimIndent()
    )
}
