import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
    id(Plugins.MAVEN_PUBLISH)
}

val artifactID = "flank-scripts"

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
// <breaking change>.<feature added>.<fix/minor change>
version = "1.9.17"
group = "com.github.flank"

application {
    mainClassName = "flank.scripts.cli.MainKt"
    applicationDefaultJvmArgs = listOf(
        "-Xmx2048m",
        "-Xms512m"
    )
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Flank/flank")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: properties["GITHUB_ACTOR"].toString()
                password = System.getenv("GITHUB_TOKEN") ?: properties["GITHUB_TOKEN"].toString()
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group.toString()
            artifactId = artifactID
            version = version

            artifact(shadowJar)

            pom {
                name.set("Flank-scripts")
                description.set("Scripts for Flank")
                url.set("https://github.com/Flank/flank")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }

            pom.withXml {
                // Remove deps since we're publishing a fat jar
                val pom = asNode()
                val depNode: groovy.util.NodeList = pom.get("dependencies") as groovy.util.NodeList
                for (child in depNode) {
                    pom.remove(child as groovy.util.Node)
                }
            }
        }
    }
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

dependencies {
    implementation(Dependencies.KOTLIN_SERIALIZATION)
    implementation(project(":common"))
    implementation(Dependencies.CLIKT)
    implementation(Dependencies.JCABI_GITHUB)
    implementation(Dependencies.SLF4J_NOP)
    implementation(Dependencies.GLASSFISH_JSON)
    implementation(Dependencies.Fuel.COROUTINES)

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TRUTH)
    testImplementation(Dependencies.SYSTEM_RULES)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

val prepareJar by tasks.registering(Copy::class) {
    dependsOn("shadowJar")
    from("$buildDir/libs")
    include("flank-scripts.jar")
    into("$projectDir/bash")
}

val download by tasks.registering(Exec::class) {
    commandLine(
        "gh", "release", "download", "flank-scripts-$version", "-D", System.getenv("GITHUB_WORKSPACE") ?: ".."
    )
    doLast {
        Files.copy(
            Paths.get("$artifactID.jar"),
            Paths.get("flank-scripts", "bash", "$artifactID.jar")
        )
    }
}

val checkIfVersionUpdated by tasks.registering(Exec::class) {
    group = "verification"
    commandLine("git", "fetch", "--no-tags")

    doLast {
        val changedFiles = execAndGetStdout("git", "diff", "origin/master", "HEAD", "--name-only").split("\n") +
            execAndGetStdout("git", "diff", "origin/master", "--name-only").split("\n")
        val isVersionChanged = changedFiles.any { it.startsWith("flank-scripts") }.not() ||
            (changedFiles.contains("flank-scripts/build.gradle.kts") && isVersionChangedInBuildGradle())

        if (isVersionChanged.not()) {
            throw GradleException(
                """
                   Flank scripts version is not updated, but files changed.
                   Please update version according to schema: <breaking change>.<feature added>.<fix/minor change>
                """.trimIndent()

            )
        }
    }
}

val releaseFlankScripts by tasks.registering(Exec::class) {
    dependsOn(":flank-scripts:publish")
    commandLine(
        "gh", "release", "create",
        "flank-scripts-$version", "$buildDir/libs/$artifactID.jar",
        "-t", "Flank Scripts $version",
        "-p"
    )
}

fun isVersionChangedInBuildGradle(): Boolean {

    val localResultsStream = execAndGetStdout("git", "diff", "origin/master", "HEAD", "--", "build.gradle.kts")
        .split("\n")
    val commitedResultsStream = execAndGetStdout("git", "diff", "origin/master", "--", "build.gradle.kts")
        .split("\n")
    return (commitedResultsStream + localResultsStream)
        .filter { it.startsWith("-version = ") || it.startsWith("+version = ") }
        .size >= 2
}

fun execAndGetStdout(vararg args: String): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine(*args)
        standardOutput = stdout
        workingDir = projectDir
    }
    return stdout.toString().trimEnd()
}

tasks["lintKotlin"].dependsOn(tasks["checkIfVersionUpdated"])
