import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.nio.file.Paths

plugins {
    application
    jacoco
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.MAVEN_PUBLISH)
    signing
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
}

val artifactID = "flank"

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    outputs.cacheIf { false } // to investigate if better solution is possible
    archiveClassifier.set("")
    archiveBaseName.set(artifactID)
    mergeServiceFiles()
    minimize {
        exclude(dependency(Dependencies.KOTLIN_REFLECT))
        exclude(dependency(Dependencies.JACKSON_XML))
        exclude(dependency(Dependencies.JACKSON_DATABIND))
        exclude(dependency(Dependencies.JACKSON_KOTLIN))
        exclude(dependency(Dependencies.JACKSON_YAML))
        exclude(dependency(Dependencies.GSON))
    }
    @Suppress("UnstableApiUsage")
    manifest {
        attributes(mapOf("Main-Class" to "ftl.Main"))
    }
    dependencies {
        exclude(dependency(Dependencies.TRUTH))
        exclude(dependency(Dependencies.MOCKK))
        exclude(dependency(Dependencies.JUNIT))

        exclude(dependency(Dependencies.PROGUARD))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
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
        maven {
            val staging = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            val version = System.getenv("MVN_VERSION") ?: "local-SNAPSHOT"
            url = uri(if (version.endsWith("-SNAPSHOT")) snapshotsRepoUrl else staging)
            name = "MavenCentral"
            credentials {
                username = System.getenv("MVN_CENTRAL_USER") ?: properties["MVN_CENTRAL_USER"].toString()
                password = System.getenv("MVN_CENTRAL_PASSWORD") ?: properties["MVN_CENTRAL_PASSWORD"].toString()
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.flank"
            artifactId = artifactID
            version = System.getenv("MVN_VERSION") ?: "local-SNAPSHOT"

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

signing {
    val pgpSigningKey: String = System.getenv("PGP_SIGNING_KEY") ?: properties["PGP_SIGNING_KEY"].toString()
    useInMemoryPgpKeys(pgpSigningKey, "")
    sign(publishing.publications["mavenJava"])
}

// http://www.eclemma.org/jacoco/
jacoco {
    toolVersion = "0.8.6"
}

tasks.jacocoTestReport {
    classDirectories.setFrom(
        fileTree("build/classes/kotlin/main").apply {
            exclude("**/*\$run$1.class")
            exclude("**/ftl/mock/*")
        }
    )

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

val runningOnCI get() = System.getenv("CI") != null

tasks.withType<KotlinCompile>().configureEach {
    // https://devcenter.bitrise.io/builds/available-environment-variables/
    kotlinOptions.allWarningsAsErrors = runningOnCI
}

application {
    // unfortunately shadowJar task seems not to be working correctly with new property based approach
    mainClassName = "ftl.Main"
}

repositories {
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    jcenter()
}

// Output full test results to console
// Avoids having to read the HTML report
tasks.withType<Test> {
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(project(":common"))
    implementation(Dependencies.SENTRY)
    implementation(Dependencies.MIXPANEL)

    implementation(Dependencies.DD_PLIST)
    implementation(Dependencies.DEX_TEST_PARSER)

    implementation(Dependencies.JACKSON_KOTLIN)
    implementation(Dependencies.JACKSON_YAML)
    implementation(Dependencies.JACKSON_XML)

    implementation(Dependencies.GOOGLE_NIO)
    implementation(Dependencies.GOOGLE_AUTH)
    implementation(Dependencies.GOOGLE_STORAGE)
    implementation(Dependencies.GOOGLE_TOOLRESULTS)

    implementation(Dependencies.KTOR_SERVER_CORE)
    implementation(Dependencies.KTOR_SERVER_NETTY)
    implementation(Dependencies.KTOR_GSON)

    implementation(Dependencies.KOTLIN_COROUTINES_CORE)

    implementation(Dependencies.LOGBACK)

    implementation(Dependencies.PICOCLI)
    annotationProcessor(Dependencies.PICOCLI_CODEGEN)

    implementation(Dependencies.KOTLIN_LOGGING)
    implementation(Dependencies.COMMON_TEXT)
    implementation(Dependencies.JANSI)

    // NOTE: iOS support isn't in the public artifact. Use testing jar generated from the private gcloud CLI json
    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-testing%22
    // compile("com.google.apis:google-api-services-testing:v1-rev30-1.23.0")
    implementation(project(":firebase_apis:test_api"))

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.SYSTEM_RULES)
    testImplementation(Dependencies.TRUTH)
    testImplementation(Dependencies.MOCKK)
}

buildscript {
    repositories {
        mavenLocal()
        jcenter()
        google()
    }
    dependencies {
        classpath(Dependencies.PROGUARD)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// https://github.com/gradle/kotlin-dsl/blob/master/samples/task-dependencies/build.gradle.kts#L41
// https://github.com/codecov/example-gradle/blob/master/build.gradle#L25

val updateFlank by tasks.registering(Exec::class) {
    group = "Build"
    description = "Update flank jar"
    commandLine = if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        doLast {
            copy { // due to security permissions copying files is restricted via bat files
                from("./test_runner/build/libs/flank.jar")
                into("./test_runner/bash/")
            }
        }
        listOf("./bash/update_flank.bat")
    } else {
        listOf("./bash/update_flank.sh")
    }
}

val flankFullRun by tasks.registering(Exec::class) {
    dependsOn(tasks["clean"], tasks["check"])
    // currently IT run only on CI, implement support to enable local run
    dependsOn(":integration_tests:test")
    group = "Build"
    description = "Perform full test_runner run"
    commandLine = listOf("./bash/update_flank.sh")
}

tasks.create("applyProguard", proguard.gradle.ProGuardTask::class.java) {
    dependsOn("updateFlank")
    description = "Apply proguard to flank and create a minimized jar"
    dontwarn()
    injars("./build/libs/flank.jar")
    outjars("./build/libs/flank-proguard.jar")
    libraryjars("${System.getProperty("java.home")}/lib/rt.jar")
    libraryjars("./build/libs/flank-sources.jar")
    configuration("./proguard/config.pro")
    doLast {
        copy {
            from(file("$buildDir/libs/flank-proguard.jar"))
            into(file("./bash/"))
        }
    }
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
        application.mainClass.get(),
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

// tasks.assemble {
// dependsOn(processCliAsciiDoc)
// }
// end --- ASCII doc generation ---

val updateVersion by tasks.registering {
    shouldRunAfter(tasks.processResources)
    if (!runningOnCI) doLast {
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

val resolveArtifacts by tasks.registering {
    dependsOn(":flank-scripts:prepareJar")
    group = "verification"
    doLast {
        val flankScriptsRunnerName = if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows)
            "flankScripts.bat" else "flankScripts"
        val flankScriptsPath = Paths.get("flank-scripts", "bash", flankScriptsRunnerName).toString()
        val rootFlankScriptsPath = rootDir.resolve(flankScriptsPath).absolutePath
        println(rootFlankScriptsPath)
        exec {
            commandLine(rootFlankScriptsPath, "testArtifacts", "-p", rootDir.absolutePath, "resolve")
            workingDir = rootDir
        }
    }
}

tasks.test {
    maxHeapSize = "3072m"
    minHeapSize = "512m"
    dependsOn(resolveArtifacts)
}
