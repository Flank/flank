import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
    id(Plugins.MAVEN_PUBLISH)
}

val artifactID = "flank-wrapper"
val runnerClass = "com.github.flank.wrapper.MainKt"
val shadowJar: ShadowJar by tasks

shadowJar.apply {
    archiveClassifier.set("")
    archiveFileName.set("$artifactID.jar")
    mergeServiceFiles()

    @Suppress("UnstableApiUsage")
    manifest {
        attributes(mapOf("Main-Class" to runnerClass))
    }
}
// <breaking change>.<feature added>.<fix/minor change>
version = "1.0.0"
group = "com.github.flank"

repositories {
    mavenCentral()
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
                name.set("Flank-wrapper")
                description.set("Wrapper to run latest version of Flank")
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

dependencies {
    implementation(project(":common"))
    implementation(Dependencies.Fuel.CORE)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.TRUTH)
}

val releaseFlankWrapper by tasks.registering(Exec::class) {
    dependsOn(":flank_wrapper:publish")
    commandLine(
        "gh", "release", "create",
        "flank-wrapper-$version", "$buildDir/libs/$artifactID.jar",
        "-t", "Flank Wrapper $version",
        "-p"
    )
}


application {
    mainClass.set(runnerClass)
}

// TODO replace with plugin in #2063
val checkIfVersionUpdated by tasks.registering(Exec::class) {
    group = "verification"
    commandLine("git", "fetch", "--no-tags")

    doLast {
        val changedFiles = execAndGetStdout("git", "diff", "origin/master", "HEAD", "--name-only").split("\n") +
            execAndGetStdout("git", "diff", "origin/master", "--name-only").split("\n")
        val isVersionChanged = changedFiles.any { it.startsWith("flank_wrapper") }.not() ||
            (changedFiles.contains("flank_wrapper/build.gradle.kts") && isVersionChangedInBuildGradle())

        if (isVersionChanged.not()) {
            throw GradleException(
                """
                   Flank wrapper version is not updated, but files changed.
                   Please update version according to schema: <breaking change>.<feature added>.<fix/minor change>
                """.trimIndent()

            )
        }
    }
}

val prepareJar by tasks.registering(Copy::class) {
    dependsOn("shadowJar")
    from("$buildDir/libs")
    include("flank_wrapper.jar")
    into("$projectDir")
}


tasks["lintKotlin"].dependsOn(checkIfVersionUpdated)
