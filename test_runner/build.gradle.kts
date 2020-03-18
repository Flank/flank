import com.jfrog.bintray.gradle.BintrayExtension
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

group = "test_runner"
version = "SNAPSHOT"

plugins {
    application
    jacoco
    kotlin("jvm") version Versions.KOTLIN

    id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
    id("com.jfrog.bintray") version "1.8.4"
    id("maven-publish")
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
        version(closureOf<BintrayExtension.VersionConfig>{
            name = System.getenv("MVN_VERSION")
            vcsTag = System.getenv("MVN_REVISION")
            released = Date().toString()

        })
    })
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            groupId = "flank"
            artifactId = "flank"
            version = System.getenv("MVN_VERSION")

            setArtifacts(listOf("build/libs/flank-SNAPSHOT.jar"))

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

tasks.withType<KotlinCompile>().configureEach {
    // https://devcenter.bitrise.io/builds/available-environment-variables/
    val runningOnBitrise = System.getenv("BITRISE_IO") != null
    kotlinOptions.allWarningsAsErrors = runningOnBitrise
}

apply {
    plugin("kotlin")
}

application {
    mainClassName = "main"
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
    testImplementation(Libs.MOCKITO_INLINE)
    implementation(Libs.SYSTEM_RULES)
    testImplementation(Libs.TRUTH)
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

task("fatJar", type = Jar::class) {
    archiveBaseName.set("flank")
    @Suppress("UnstableApiUsage")
    manifest {
        attributes.apply {
            put("Main-Class", "ftl.Main")
        }
    }
    from(configurations.runtimeClasspath.get().map { file ->
        if (file.isDirectory) file else zipTree(file)
    })
    with(tasks["jar"] as CopySpec)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// https://github.com/gradle/kotlin-dsl/blob/master/samples/task-dependencies/build.gradle.kts#L41
// https://github.com/codecov/example-gradle/blob/master/build.gradle#L25
tasks["check"].dependsOn(tasks["jacocoTestReport"], tasks["detekt"])
