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
        vcsUrl = "https://github.com/TestArmada/flank.git"
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
    toolVersion = "0.8.3"
}

// Html report created in ./build/reports/jacoco/test/html/index.html
// ./gradlew clean check jacocoTestReport
tasks.withType<JacocoReport> {
    doFirst {
        // runBlocking {} produces generated Kotlin code. Ignore that.
        classDirectories.setFrom(
            fileTree("build/classes/kotlin/main").apply {
                exclude("**/*\$run$1.class")
            })

        reports {
            html.isEnabled = true
            xml.isEnabled = true
        }
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
    implementation(Libs.KOTLIN_STD_LIB)
    implementation(Libs.KOTLIN_COROUTINES_CORE)

    implementation("com.bugsnag:bugsnag:3.+")

    // https://github.com/remkop/picocli/releases
    implementation("info.picocli:picocli:4.0.4")

    // https://github.com/google/gson/releases
    implementation("com.google.code.gson:gson:2.8.6")

    // https://github.com/3breadt/dd-plist/releases
    implementation("com.googlecode.plist:dd-plist:1.22")

    // https://search.maven.org/search?q=a:google-cloud-storage%20g:com.google.cloud
    implementation("com.google.cloud:google-cloud-storage:1.96.0")

    // https://search.maven.org/search?q=a:google-api-services-toolresults%20g:com.google.apis
    implementation("com.google.apis:google-api-services-toolresults:v1beta3-rev20190923-1.30.3")

    // https://github.com/linkedin/dex-test-parser/releases
    implementation("com.linkedin.dextestparser:parser:2.1.1")

    // NOTE: iOS support isn't in the public artifact. Use testing jar generated from the private gcloud CLI json
    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-testing%22
    // compile("com.google.apis:google-api-services-testing:v1-rev30-1.23.0")
    implementation(project("test_api"))

    // yaml config
    // https://search.maven.org/search?q=a:jackson-databind%20g:com.fasterxml.jackson.core
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.JACKSON}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.JACKSON}")

    // https://github.com/FasterXML/jackson-dataformat-xml/releases
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.JACKSON}")

    // https://github.com/FasterXML/woodstox/releases
    implementation("com.fasterxml.woodstox:woodstox-core:6.0.1")

    // https://github.com/googleapis/google-auth-library-java/releases
    // NOTE: https://github.com/googleapis/google-oauth-java-client is End of Life and replaced by google-auth-library-java
    // https://github.com/googleapis/google-oauth-java-client/issues/251#issuecomment-504565533
    implementation("com.google.auth:google-auth-library-oauth2-http:0.18.0")

    // https://github.com/jhy/jsoup/releases
    testImplementation("org.jsoup:jsoup:1.12.1")
    testImplementation(Libs.JUNIT)
    // https://github.com/google/truth/releases
    testImplementation("com.google.truth:truth:1.0")
    // https://github.com/stefanbirkner/system-rules/releases
    testImplementation("com.github.stefanbirkner:system-rules:1.19.0")

    // https://github.com/ktorio/ktor/releases
    val ktorVersion = "1.2.5"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")

    // https://github.com/qos-ch/logback/releases
    implementation("ch.qos.logback:logback-classic:1.2.3")
    // mockito-inline is used to mock final classes
    // https://github.com/mockito/mockito/releases
    testImplementation("org.mockito:mockito-inline:3.1.2")
    // https://github.com/square/okhttp/releases
    testImplementation("com.squareup.okhttp3:okhttp:4.2.2")

    // todo: move to testImplementation once DI is implemented https://github.com/TestArmada/flank/issues/248
    // https://search.maven.org/search?q=a:google-cloud-nio%20g:com.google.cloud
    implementation("com.google.cloud:google-cloud-nio:0.114.0-alpha")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
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
    baseName = "flank"
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
