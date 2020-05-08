import com.jfrog.bintray.gradle.BintrayExtension
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.ByteArrayOutputStream

plugins {
    application
    jacoco
    kotlin("jvm") version Versions.KOTLIN

    id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
    id("com.jfrog.bintray") version Versions.BINTRAY
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version Versions.SHADOW
}

val artifactID = "flank"

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    archiveClassifier.set("")
    archiveBaseName.set(artifactID)
    mergeServiceFiles()
    @Suppress("UnstableApiUsage")
    manifest {
        attributes(mapOf("Main-Class" to "ftl.Main"))
    }
}

// https://bintray.com/flank/maven
// https://github.com/bintray/gradle-bintray-plugin#readme
bintray {
    user = System.getenv("MVN_USER")
    key = System.getenv("MVN_KEY")
    publish = true
    setPublications("mavenJava")
    pkg(closureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "flank"
        userOrg = "flank"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/Flank/flank.git"
        version(closureOf<BintrayExtension.VersionConfig> {
            name = System.getenv("MVN_VERSION")
            vcsTag = System.getenv("MVN_REVISION")
            released = Date().toString()
        })
    })
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.flank"
            artifactId = artifactID
            version = System.getenv("MVN_VERSION")

            artifact(shadowJar)
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("Flank")
                description.set("Massively parallel Android and iOS test runner for Firebase Test Lab")
                url.set("https://github.com/flank/flank")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("bootstraponline")
                        name.set("bootstrap online")
                        email.set("code@bootstraponline.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/flank/flank.git")
                    developerConnection.set("scm:git:ssh://github.com:flank/flank.git")
                    url.set("https://github.com/flank/flank")
                }
            }

            pom.withXml {
                // Remove deps since we're publishing a fat jar
                val pom = asNode()
                val depNode: NodeList = pom.get("dependencies") as NodeList
                for (child in depNode) {
                    pom.remove(child as Node)
                }
            }
        }
    }
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("../config/detekt.yml")
    reports {
        xml {
            enabled = false
        }
        html {
            enabled = true
        }
    }
}

// http://www.eclemma.org/jacoco/
jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    classDirectories.setFrom(
        fileTree("build/classes/kotlin/main").apply {
            exclude("**/*\$run$1.class")
            exclude("**/ftl/mock/*")
        })

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

val runningOnBitrise get() = System.getenv("BITRISE_IO") != null

tasks.withType<KotlinCompile>().configureEach {
    // https://devcenter.bitrise.io/builds/available-environment-variables/
    kotlinOptions.allWarningsAsErrors = runningOnBitrise
}


application {
    mainClassName = "ftl.Main"
}

repositories {
    maven(url = "http://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    jcenter()
}

// Output full test results to console
// Avoids having to read the HTML report
tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(Libs.BUGSNAG)

    implementation(Libs.DD_PLIST)
    implementation(Libs.DEX_TEST_PARSER)

    implementation(Libs.GSON)

    implementation(Libs.JACKSON_DATABIND)
    implementation(Libs.JACKSON_KOTLIN)
    implementation(Libs.JACKSON_YAML)
    implementation(Libs.JACKSON_XML)

    implementation(Libs.GOOGLE_NIO)
    implementation(Libs.GOOGLE_AUTH)
    implementation(Libs.GOOGLE_STORAGE)
    implementation(Libs.GOOGLE_TOOLRESULTS)

    implementation(Libs.KTOR_SERVER_CORE)
    implementation(Libs.KTOR_SERVER_NETTY)
    implementation(Libs.KTOR_GSON)

    implementation(Libs.KOTLIN_STD_LIB)
    implementation(Libs.KOTLIN_COROUTINES_CORE)

    implementation(Libs.LOGBACK)

    implementation(Libs.PICOCLI)
    annotationProcessor(Libs.PICOCLI_CODEGEN)

    implementation(Libs.WOODSTOX)

    implementation(Libs.KOTLIN_LOGGING)

    // NOTE: iOS support isn't in the public artifact. Use testing jar generated from the private gcloud CLI json
    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-testing%22
    // compile("com.google.apis:google-api-services-testing:v1-rev30-1.23.0")
    implementation(project("test_api"))

    implementation(Libs.JSOUP)
    implementation(Libs.OKHTTP)

    detektPlugins(Libs.DETEKT_FORMATTING)

    testImplementation(Libs.JUNIT)
    implementation(Libs.SYSTEM_RULES)
    testImplementation(Libs.TRUTH)
    testImplementation(Libs.MOCKK)

    implementation(Libs.COMMON_TEXT)
}

// Fix Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.hash.Hashing.crc32c()Lcom/google/common/hash/HashFunction;
// https://stackoverflow.com/a/45286710
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:25.1-jre")
        force(Libs.KOTLIN_REFLECT)
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// https://github.com/gradle/kotlin-dsl/blob/master/samples/task-dependencies/build.gradle.kts#L41
// https://github.com/codecov/example-gradle/blob/master/build.gradle#L25
tasks["check"].dependsOn(tasks["jacocoTestReport"], tasks["detekt"])

tasks.create("updateFlank", Exec::class.java) {
    description = "Update flank jar"
    commandLine = listOf("./bash/update_flank.sh")
}

// begin --- ASCII doc generation ---
val generateCliAsciiDoc by tasks.registering(JavaExec::class) {
    dependsOn(tasks.classes)
    classpath(
        configurations.compile,
        configurations.annotationProcessor,
        sourceSets["main"].runtimeClasspath
    )
    group = "Documentation"
    description = "Generate AsciiDoc manpage"
    main = "picocli.codegen.docgen.manpage.ManPageGenerator"
    args = listOf(
        application.mainClassName,
        "--outdir=${project.rootDir}/docs/ascii/",
        "-v"
    )
}

val processCliAsciiDoc by tasks.registering {
    dependsOn(generateCliAsciiDoc)
    group = "Documentation"
    doLast {
        File("${project.rootDir}/docs/ascii").listFiles()?.forEach { file ->
            file.apply {
                readLines().run {
                    val toRemove = IntRange(
                        indexOf("// tag::picocli-generated-man-section-header[]"),
                        indexOf("// end::picocli-generated-man-section-header[]")
                    )
                    filterIndexed { index, _ -> index !in toRemove }
                }.joinToString("\n").let { text ->
                    writeText(text)
                }
            }
        }
    }
}

tasks.assemble {
    dependsOn(processCliAsciiDoc)
}
// end --- ASCII doc generation ---

val updateVersion by tasks.registering {
    shouldRunAfter(tasks.processResources)
    if (!runningOnBitrise) doLast {
        File("$buildDir/resources/main/version.txt").writeText("local_snapshot")
        File("$buildDir/resources/main/revision.txt").writeText(execAndGetStdout("git", "rev-parse", "HEAD"))
    }
}

tasks.classes {
    dependsOn(updateVersion)
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
