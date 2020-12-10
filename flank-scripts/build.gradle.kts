import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.jfrog.bintray.gradle.BintrayExtension
import java.util.*
import java.nio.file.Paths
import java.io.ByteArrayOutputStream

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
    id(Plugins.DETEKT_PLUGIN)
    id(Plugins.MAVEN_PUBLISH)
    id(Plugins.JFROG_BINTRAY)
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
version = "1.1.6"
group = "com.github.flank"

application {
    mainClassName = "flank.scripts.MainKt"
    applicationDefaultJvmArgs = listOf(
        "-Xmx2048m",
        "-Xms512m"
    )
}
bintray {
    user = System.getenv("JFROG_USER") ?: properties["JFROG_USER"].toString()
    key = System.getenv("JFROG_API_KEY") ?: properties["JFROG_API_KEY"].toString()
    publish = true
    override = true
    setPublications("mavenJava")
    pkg(closureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = artifactID
        userOrg = "flank"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/Flank/flank.git"
        version(closureOf<BintrayExtension.VersionConfig> {
            name = version.name
            released = Date().toString()
        })
    })
}
publishing {
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
    include("flank-scripts.jar")
    into("$projectDir/bash")
}

tasks.register("download") {
    val sourceUrl = "https://dl.bintray.com/flank/maven/com/github/flank/$artifactID/$version/$artifactID-$version.jar"
    val destinationFile = Paths.get("bash", "flank-scripts.jar").toFile()
    ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destinationFile))
}

val checkIfVersionUpdated by tasks.registering(Exec::class) {
    group = "verification"
    commandLine("git", "fetch", "--no-tags")

    doLast {
        val changedFiles = execAndGetStdout("git", "diff", "origin/master", "HEAD", "--name-only").split("\n") +
            execAndGetStdout("git", "diff", "origin/master", "--name-only").split("\n")
        val isVersionChanged = changedFiles.any { it.startsWith("flank-scripts") }.not()
            || (changedFiles.contains("flank-scripts/build.gradle.kts") && isVersionChangedInBuildGradle())

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

tasks["detekt"].dependsOn(tasks["checkIfVersionUpdated"])
